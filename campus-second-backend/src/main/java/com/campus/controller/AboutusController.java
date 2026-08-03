package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.Aboutus;
import com.campus.mapper.AboutusMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 关于我们接口
 */
@RestController
@RequestMapping("/api/aboutus")
public class AboutusController {

    @Resource
    private AboutusMapper aboutusMapper;

    @GetMapping("/detail")
    public Result<Aboutus> detail() {
        Aboutus entity = aboutusMapper.selectOne(new LambdaQueryWrapper<Aboutus>().last("LIMIT 1"));
        return Result.success(entity);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Aboutus entity) {
        aboutusMapper.updateById(entity);
        return Result.success("更新成功");
    }
}
