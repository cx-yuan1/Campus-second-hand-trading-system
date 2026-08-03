package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.dto.PageResult;
import com.campus.entity.Juanzengxinxi;
import com.campus.mapper.JuanzengxinxiMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/juanzengxinxi")
public class JuanzengxinxiController {

    @Resource
    private JuanzengxinxiMapper juanzengxinxiMapper;

    @GetMapping("/list")
    public Result<PageResult<Juanzengxinxi>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String keyword
    ) {
        Page<Juanzengxinxi> p = new Page<>(page, limit);
        LambdaQueryWrapper<Juanzengxinxi> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(Juanzengxinxi::getShangpinmingcheng, keyword);
        }
        qw.orderByDesc(Juanzengxinxi::getAddtime);
        Page<Juanzengxinxi> result = juanzengxinxiMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Juanzengxinxi entity) {
        if (entity.getJuanzengbianhao() == null || entity.getJuanzengbianhao().isEmpty()) {
            entity.setJuanzengbianhao(System.currentTimeMillis() + "");
        }
        if (entity.getJuanzengshijian() == null) {
            entity.setJuanzengshijian(LocalDate.now());
        }
        juanzengxinxiMapper.insert(entity);
        return Result.success("发布成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Juanzengxinxi entity) {
        juanzengxinxiMapper.updateById(entity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        juanzengxinxiMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
