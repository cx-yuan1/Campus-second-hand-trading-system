package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.dto.PageResult;
import com.campus.entity.Address;
import com.campus.entity.Cart;
import com.campus.entity.Juanzengxinxi;
import com.campus.entity.Orders;
import com.campus.entity.Qiugouxinxi;
import com.campus.entity.Shangpinxinxi;
import com.campus.entity.Storeup;
import com.campus.entity.Yonghu;
import com.campus.mapper.AddressMapper;
import com.campus.mapper.CartMapper;
import com.campus.mapper.JuanzengxinxiMapper;
import com.campus.mapper.OrdersMapper;
import com.campus.mapper.QiugouxinxiMapper;
import com.campus.mapper.ShangpinxinxiMapper;
import com.campus.mapper.StoreupMapper;
import com.campus.mapper.YonghuMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理接口（管理端）
 */
@RestController
@RequestMapping("/api/yonghu")
public class YonghuController {

    @Resource
    private YonghuMapper yonghuMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private StoreupMapper storeupMapper;
    @Resource
    private JuanzengxinxiMapper juanzengxinxiMapper;
    @Resource
    private QiugouxinxiMapper qiugouxinxiMapper;
    @Resource
    private ShangpinxinxiMapper shangpinxinxiMapper;

    @GetMapping("/list")
    public Result<PageResult<Yonghu>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String keyword
    ) {
        Page<Yonghu> p = new Page<>(page, limit);
        LambdaQueryWrapper<Yonghu> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like(Yonghu::getYonghuzhanghao, keyword)
                    .or().like(Yonghu::getYonghuxingming, keyword));
        }
        qw.orderByDesc(Yonghu::getId);
        Page<Yonghu> result = yonghuMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @GetMapping("/{id}")
    public Result<Yonghu> detail(@PathVariable Long id) {
        Yonghu entity = yonghuMapper.selectById(id);
        if (entity == null) return Result.error("用户不存在");
        return Result.success(entity);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Yonghu entity) {
        yonghuMapper.insert(entity);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Yonghu entity) {
        yonghuMapper.updateById(entity);
        return Result.success("更新成功");
    }

    /**
     * 用户充值（用户端调用，增加本人余额）
     */
    @PostMapping("/recharge")
    public Result<String> recharge(@RequestBody RechargeDTO dto) {
        if (dto.getUserid() == null) return Result.error("用户未登录");
        if (dto.getAmount() == null || dto.getAmount() <= 0) return Result.error("充值金额必须大于0");
        Yonghu user = yonghuMapper.selectById(dto.getUserid());
        if (user == null) return Result.error("用户不存在");
        float current = user.getMoney() != null ? user.getMoney() : 0;
        user.setMoney(current + dto.getAmount());
        yonghuMapper.updateById(user);
        return Result.success("充值成功，当前余额：" + user.getMoney());
    }

    @lombok.Data
    public static class RechargeDTO {
        private Long userid;
        private Float amount;
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Yonghu user = yonghuMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        String username = user.getYonghuzhanghao();
        List<String> reasons = new ArrayList<>();
        // 检查订单
        long ordersCount = ordersMapper.selectCount(
                new LambdaQueryWrapper<Orders>().eq(Orders::getUserid, id)
        );
        if (ordersCount > 0) reasons.add(ordersCount + " 个订单");
        // 检查收货地址
        long addressCount = addressMapper.selectCount(
                new LambdaQueryWrapper<Address>().eq(Address::getUserid, id)
        );
        if (addressCount > 0) reasons.add(addressCount + " 个收货地址");
        // 检查购物车
        long cartCount = cartMapper.selectCount(
                new LambdaQueryWrapper<Cart>().eq(Cart::getUserid, id)
        );
        if (cartCount > 0) reasons.add(cartCount + " 条购物车记录");
        // 检查收藏
        long storeupCount = storeupMapper.selectCount(
                new LambdaQueryWrapper<Storeup>().eq(Storeup::getUserid, id)
        );
        if (storeupCount > 0) reasons.add(storeupCount + " 条收藏");
        // 检查捐赠
        long juanzengCount = juanzengxinxiMapper.selectCount(
                new LambdaQueryWrapper<Juanzengxinxi>().eq(Juanzengxinxi::getYonghuzhanghao, username)
        );
        if (juanzengCount > 0) reasons.add(juanzengCount + " 条捐赠信息");
        // 检查求购
        long qiugouCount = qiugouxinxiMapper.selectCount(
                new LambdaQueryWrapper<Qiugouxinxi>().eq(Qiugouxinxi::getYonghuzhanghao, username)
        );
        if (qiugouCount > 0) reasons.add(qiugouCount + " 条求购信息");
        // 检查发布的商品
        long goodsCount = shangpinxinxiMapper.selectCount(
                new LambdaQueryWrapper<Shangpinxinxi>().eq(Shangpinxinxi::getYonghuzhanghao, username)
        );
        if (goodsCount > 0) reasons.add(goodsCount + " 个已发布商品");
        if (!reasons.isEmpty()) {
            return Result.error("该用户存在关联数据（" + String.join("、", reasons) + "），无法删除。请先处理相关数据后再删除用户。");
        }
        yonghuMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
