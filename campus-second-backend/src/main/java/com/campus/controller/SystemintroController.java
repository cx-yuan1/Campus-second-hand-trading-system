package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.Systemintro;
import com.campus.mapper.SystemintroMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 系统简介接口
 */
@RestController
@RequestMapping("/api/systemintro")
public class SystemintroController {

    @Resource
    private SystemintroMapper systemintroMapper;

    @GetMapping("/detail")
    public Result<Systemintro> detail() {
        Systemintro entity = systemintroMapper.selectOne(new LambdaQueryWrapper<Systemintro>().last("LIMIT 1"));
        return Result.success(entity);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Systemintro entity) {
        systemintroMapper.updateById(entity);
        return Result.success("更新成功");
    }
}
