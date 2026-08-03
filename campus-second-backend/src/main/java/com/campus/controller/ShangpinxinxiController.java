package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.dto.PageResult;
import com.campus.entity.Cart;
import com.campus.entity.Orders;
import com.campus.entity.Shangpinxinxi;
import com.campus.entity.Storeup;
import com.campus.mapper.CartMapper;
import com.campus.mapper.OrdersMapper;
import com.campus.mapper.ShangpinxinxiMapper;
import com.campus.mapper.StoreupMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品信息接口
 */
@RestController
@RequestMapping("/api/shangpinxinxi")
public class ShangpinxinxiController {

    @Resource
    private ShangpinxinxiMapper shangpinxinxiMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private StoreupMapper storeupMapper;

    @GetMapping("/list")
    public Result<PageResult<Shangpinxinxi>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer limit,
            @RequestParam(required = false) String shangpinfenlei,
            @RequestParam(required = false) String keyword
    ) {
        Page<Shangpinxinxi> p = new Page<>(page, limit);
        LambdaQueryWrapper<Shangpinxinxi> qw = new LambdaQueryWrapper<>();
        if (shangpinfenlei != null && !shangpinfenlei.isEmpty()) {
            qw.eq(Shangpinxinxi::getShangpinfenlei, shangpinfenlei);
        }
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like(Shangpinxinxi::getShangpinmingcheng, keyword)
                    .or().like(Shangpinxinxi::getShangpinbianhao, keyword));
        }
        qw.orderByDesc(Shangpinxinxi::getAddtime);
        Page<Shangpinxinxi> result = shangpinxinxiMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @GetMapping("/{id}")
    public Result<Shangpinxinxi> detail(@PathVariable Long id) {
        Shangpinxinxi entity = shangpinxinxiMapper.selectById(id);
        if (entity == null) {
            return Result.error("商品不存在");
        }
        entity.setClicktime(LocalDateTime.now());
        entity.setClicknum((entity.getClicknum() == null ? 0 : entity.getClicknum()) + 1);
        shangpinxinxiMapper.updateById(entity);
        return Result.success(entity);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Shangpinxinxi entity) {
        if (entity.getShangpinbianhao() == null || entity.getShangpinbianhao().isEmpty()) {
            entity.setShangpinbianhao(System.currentTimeMillis() + "");
        }
        if (entity.getFabushijian() == null) entity.setFabushijian(LocalDate.now());
        if (entity.getClicknum() == null) entity.setClicknum(0);
        shangpinxinxiMapper.insert(entity);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Shangpinxinxi entity) {
        shangpinxinxiMapper.updateById(entity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Shangpinxinxi goods = shangpinxinxiMapper.selectById(id);
        if (goods == null) {
            return Result.error("商品不存在");
        }
        List<String> reasons = new ArrayList<>();
        // 检查购物车
        long cartCount = cartMapper.selectCount(
                new LambdaQueryWrapper<Cart>().eq(Cart::getGoodid, id)
        );
        if (cartCount > 0) reasons.add(cartCount + " 条购物车记录");
        // 检查订单
        long ordersCount = ordersMapper.selectCount(
                new LambdaQueryWrapper<Orders>().eq(Orders::getGoodid, id)
        );
        if (ordersCount > 0) reasons.add(ordersCount + " 个订单");
        // 检查收藏（商品表名为 shangpinxinxi）
        long storeupCount = storeupMapper.selectCount(
                new LambdaQueryWrapper<Storeup>()
                        .eq(Storeup::getRefid, id)
                        .eq(Storeup::getTablename, "shangpinxinxi")
        );
        if (storeupCount > 0) reasons.add(storeupCount + " 条收藏");
        if (!reasons.isEmpty()) {
            return Result.error("该商品存在关联数据（" + String.join("、", reasons) + "），无法删除。请先处理相关数据后再删除商品。");
        }
        shangpinxinxiMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
