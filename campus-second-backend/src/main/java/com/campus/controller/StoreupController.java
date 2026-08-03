package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.Shangpinxinxi;
import com.campus.entity.Storeup;
import com.campus.mapper.ShangpinxinxiMapper;
import com.campus.mapper.StoreupMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/storeup")
public class StoreupController {

    @Resource
    private StoreupMapper storeupMapper;

    @Resource
    private ShangpinxinxiMapper shangpinxinxiMapper;

    @GetMapping("/list")
    public Result<List<Storeup>> list(@RequestParam Long userid) {
        List<Storeup> list = storeupMapper.selectList(
                new LambdaQueryWrapper<Storeup>()
                        .eq(Storeup::getUserid, userid)
                        .eq(Storeup::getType, "1")
        );
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Storeup entity) {
        Storeup exist = storeupMapper.selectOne(
                new LambdaQueryWrapper<Storeup>()
                        .eq(Storeup::getUserid, entity.getUserid())
                        .eq(Storeup::getRefid, entity.getRefid())
                        .eq(Storeup::getTablename, "shangpinxinxi")
        );
        if (exist != null) {
            return Result.error("已收藏");
        }
        Shangpinxinxi good = shangpinxinxiMapper.selectById(entity.getRefid());
        if (good != null) {
            entity.setName(good.getShangpinmingcheng());
            entity.setPicture(good.getTupian() != null ? good.getTupian().split(",")[0] : null);
            entity.setTablename("shangpinxinxi");
            entity.setType("1");
        }
        storeupMapper.insert(entity);
        return Result.success("收藏成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, @RequestParam Long userid) {
        storeupMapper.delete(new LambdaQueryWrapper<Storeup>()
                .eq(Storeup::getId, id)
                .eq(Storeup::getUserid, userid));
        return Result.success("取消收藏");
    }
}
