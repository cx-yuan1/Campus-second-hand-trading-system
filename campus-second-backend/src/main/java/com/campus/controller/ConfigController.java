package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.Config;
import com.campus.mapper.ConfigMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 配置接口（轮播图等）
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Resource
    private ConfigMapper configMapper;

    @GetMapping("/list")
    public Result<List<Config>> list() {
        List<Config> list = configMapper.selectList(new LambdaQueryWrapper<Config>().orderByAsc(Config::getId));
        return Result.success(list);
    }

    @GetMapping("/banner")
    public Result<List<Config>> banner() {
        List<Config> list = configMapper.selectList(
                new LambdaQueryWrapper<Config>()
                        .like(Config::getName, "picture")
                        .orderByAsc(Config::getId)
        );
        return Result.success(list);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Config entity) {
        configMapper.updateById(entity);
        return Result.success("更新成功");
    }
}
