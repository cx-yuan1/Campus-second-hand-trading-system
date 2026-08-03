package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.Cart;
import com.campus.entity.Shangpinxinxi;
import com.campus.mapper.CartMapper;
import com.campus.mapper.ShangpinxinxiMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private ShangpinxinxiMapper shangpinxinxiMapper;

    @GetMapping("/list")
    public Result<List<Cart>> list(@RequestParam Long userid) {
        List<Cart> list = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>().eq(Cart::getUserid, userid)
        );
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Cart entity) {
        Cart exist = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserid, entity.getUserid())
                        .eq(Cart::getGoodid, entity.getGoodid())
        );
        if (exist != null) {
            exist.setBuynumber(exist.getBuynumber() + (entity.getBuynumber() != null ? entity.getBuynumber() : 1));
            cartMapper.updateById(exist);
        } else {
            Shangpinxinxi good = shangpinxinxiMapper.selectById(entity.getGoodid());
            if (good != null) {
                entity.setGoodname(good.getShangpinmingcheng());
                entity.setPicture(good.getTupian() != null ? good.getTupian().split(",")[0] : null);
                entity.setPrice(good.getPrice());
                entity.setDiscountprice(good.getPrice());
                entity.setYonghuzhanghao(good.getYonghuzhanghao());
                entity.setTablename("shangpinxinxi");
            }
            if (entity.getBuynumber() == null) entity.setBuynumber(1);
            cartMapper.insert(entity);
        }
        return Result.success("添加成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, @RequestParam Long userid) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getId, id)
                .eq(Cart::getUserid, userid));
        return Result.success("删除成功");
    }
}
