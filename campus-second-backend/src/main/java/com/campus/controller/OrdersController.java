package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.dto.PageResult;
import com.campus.entity.Address;
import com.campus.entity.Cart;
import com.campus.entity.Orders;
import com.campus.entity.Shangpinxinxi;
import com.campus.entity.Yonghu;
import com.campus.mapper.AddressMapper;
import com.campus.mapper.CartMapper;
import com.campus.mapper.OrdersMapper;
import com.campus.mapper.ShangpinxinxiMapper;
import com.campus.mapper.YonghuMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private ShangpinxinxiMapper shangpinxinxiMapper;
    @Resource
    private YonghuMapper yonghuMapper;

    @GetMapping("/list")
    public Result<PageResult<Orders>> list(
            @RequestParam(required = false) Long userid,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String status
    ) {
        Page<Orders> p = new Page<>(page, limit);
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        if (userid != null) {
            qw.eq(Orders::getUserid, userid);
        }
        if (status != null && !status.isEmpty()) {
            qw.eq(Orders::getStatus, status);
        }
        qw.orderByDesc(Orders::getAddtime);
        Page<Orders> result = ordersMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Orders entity) {
        String orderid = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + (int) (Math.random() * 1000);
        entity.setOrderid(orderid);
        entity.setTablename("shangpinxinxi");
        if (entity.getDiscountprice() == null) entity.setDiscountprice(entity.getPrice());
        if (entity.getDiscounttotal() == null) entity.setDiscounttotal(entity.getTotal());
        if (entity.getType() == null) entity.setType(1);
        entity.setStatus("待付款");
        ordersMapper.insert(entity);
        return Result.success(orderid);
    }

    /** 合法状态流转：待付款->待发货|已取消；待发货->待收货|已取消|已退款；待收货->已完成|已退款；已完成->已退款 */
    private static final Set<String> VALID_TRANSITIONS = new HashSet<>(Arrays.asList(
            "待付款-待发货", "待付款-已取消",
            "待发货-待收货", "待发货-已取消", "待发货-已退款",
            "待收货-已完成", "待收货-已退款",
            "已完成-已退款"
    ));

    @PutMapping("/updateStatus")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateStatus(@RequestParam Long id, @RequestParam String status) {
        Orders entity = ordersMapper.selectById(id);
        if (entity == null) return Result.error("订单不存在");
        String oldStatus = entity.getStatus();
        String key = oldStatus + "-" + status;
        if (!VALID_TRANSITIONS.contains(key)) {
            return Result.error("不允许从【" + oldStatus + "】变更为【" + status + "】");
        }
        entity.setStatus(status);
        ordersMapper.updateById(entity);

        // 付款确认（待付款 -> 待发货）：扣减库存和买家余额
        if ("待付款".equals(oldStatus) && "待发货".equals(status)) {
            float amount = entity.getDiscounttotal() != null ? entity.getDiscounttotal() : 0;
            if (amount <= 0) return Result.error("订单金额异常");
            Yonghu buyer = yonghuMapper.selectById(entity.getUserid());
            if (buyer == null) return Result.error("用户不存在");
            float balance = buyer.getMoney() != null ? buyer.getMoney() : 0;
            if (balance < amount) return Result.error("余额不足，当前余额：¥" + balance + "，请先充值");
            buyer.setMoney(balance - amount);
            yonghuMapper.updateById(buyer);

            Shangpinxinxi good = shangpinxinxiMapper.selectById(entity.getGoodid());
            if (good != null && good.getAlllimittimes() != null) {
                int buyNum = entity.getBuynumber() != null ? entity.getBuynumber() : 1;
                int stock = good.getAlllimittimes();
                if (stock < buyNum) return Result.error("商品【" + good.getShangpinmingcheng() + "】库存不足，当前库存：" + stock);
                good.setAlllimittimes(stock - buyNum);
                shangpinxinxiMapper.updateById(good);
            }
        }

        // 确认收货（待收货 -> 已完成）：将款项转入卖家余额
        if ("待收货".equals(oldStatus) && "已完成".equals(status)) {
            float amount = entity.getDiscounttotal() != null ? entity.getDiscounttotal() : 0;
            String sellerAccount = entity.getYonghuzhanghao();
            if (amount > 0 && sellerAccount != null && !sellerAccount.isEmpty()) {
                Yonghu seller = yonghuMapper.selectOne(new LambdaQueryWrapper<Yonghu>().eq(Yonghu::getYonghuzhanghao, sellerAccount));
                if (seller != null) {
                    float sellerBalance = seller.getMoney() != null ? seller.getMoney() : 0;
                    seller.setMoney(sellerBalance + amount);
                    yonghuMapper.updateById(seller);
                }
            }
        }

        // 取消/退款时加回库存和买家余额；若卖家已收款（已完成状态），需从卖家扣回
        if (("已取消".equals(status) || "已退款".equals(status))
                && ("待发货".equals(oldStatus) || "待收货".equals(oldStatus) || "已完成".equals(oldStatus))) {
            float amount = entity.getDiscounttotal() != null ? entity.getDiscounttotal() : 0;
            Yonghu buyer = yonghuMapper.selectById(entity.getUserid());
            if (buyer != null && amount > 0) {
                float balance = buyer.getMoney() != null ? buyer.getMoney() : 0;
                buyer.setMoney(balance + amount);
                yonghuMapper.updateById(buyer);
            }
            // 已完成->退款：卖家已收款，需从卖家余额扣回
            if ("已完成".equals(oldStatus) && amount > 0) {
                String sellerAccount = entity.getYonghuzhanghao();
                if (sellerAccount != null && !sellerAccount.isEmpty()) {
                    Yonghu seller = yonghuMapper.selectOne(new LambdaQueryWrapper<Yonghu>().eq(Yonghu::getYonghuzhanghao, sellerAccount));
                    if (seller != null) {
                        float sellerBalance = seller.getMoney() != null ? seller.getMoney() : 0;
                        seller.setMoney(Math.max(0, sellerBalance - amount));
                        yonghuMapper.updateById(seller);
                    }
                }
            }
            Shangpinxinxi good = shangpinxinxiMapper.selectById(entity.getGoodid());
            if (good != null && good.getAlllimittimes() != null) {
                int buyNum = entity.getBuynumber() != null ? entity.getBuynumber() : 1;
                good.setAlllimittimes(good.getAlllimittimes() + buyNum);
                shangpinxinxiMapper.updateById(good);
            }
        }
        return Result.success("更新成功");
    }

    /**
     * 购物车结算：创建订单、清空购物车。库存和余额在用户确认付款时扣减。
     */
    @PostMapping("/checkout")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> checkout(@RequestBody CheckoutDTO dto) {
        if (dto.getUserid() == null) return Result.error("用户未登录");
        if (dto.getAddressId() == null) return Result.error("请选择收货地址");
        if (dto.getCartIds() == null || dto.getCartIds().isEmpty()) return Result.error("购物车为空");

        Address addr = addressMapper.selectById(dto.getAddressId());
        if (addr == null || !addr.getUserid().equals(dto.getUserid())) {
            return Result.error("收货地址无效");
        }

        List<Cart> cartList = cartMapper.selectBatchIds(dto.getCartIds());
        if (cartList.isEmpty()) return Result.error("购物车数据无效");

        for (Cart cart : cartList) {
            if (!cart.getUserid().equals(dto.getUserid())) {
                return Result.error("购物车数据异常");
            }
            Shangpinxinxi good = shangpinxinxiMapper.selectById(cart.getGoodid());
            if (good == null) return Result.error("商品【" + cart.getGoodname() + "】已下架");
            int stock = good.getAlllimittimes() != null ? good.getAlllimittimes() : 0;
            int buyNum = cart.getBuynumber() != null ? cart.getBuynumber() : 1;
            if (stock < buyNum) {
                return Result.error("商品【" + cart.getGoodname() + "】库存不足，当前库存：" + stock);
            }
        }

        String orderid = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + (int) (Math.random() * 1000);
        for (int i = 0; i < cartList.size(); i++) {
            Cart cart = cartList.get(i);
            String oid = cartList.size() > 1 ? orderid + "-" + (i + 1) : orderid;
            Orders order = new Orders();
            order.setOrderid(oid);
            order.setTablename("shangpinxinxi");
            order.setUserid(dto.getUserid());
            order.setGoodid(cart.getGoodid());
            order.setGoodname(cart.getGoodname());
            order.setPicture(cart.getPicture());
            order.setBuynumber(cart.getBuynumber());
            order.setPrice(cart.getPrice());
            Float discountPrice = cart.getDiscountprice() != null ? cart.getDiscountprice() : cart.getPrice();
            order.setDiscountprice(discountPrice);
            int buyNum = cart.getBuynumber() != null ? cart.getBuynumber() : 1;
            order.setTotal(cart.getPrice() * buyNum);
            order.setDiscounttotal(discountPrice * buyNum);
            order.setType(1);
            order.setStatus("待付款");
            order.setAddress(addr.getAddress());
            order.setTel(addr.getPhone());
            order.setConsignee(addr.getName());
            order.setYonghuzhanghao(cart.getYonghuzhanghao());
            ordersMapper.insert(order);

            cartMapper.deleteById(cart.getId());
        }

        return Result.success(orderid);
    }

    @lombok.Data
    public static class CheckoutDTO {
        private Long userid;
        private Long addressId;
        private List<Long> cartIds;
    }
}
