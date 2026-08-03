package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.dto.PageResult;
import com.campus.entity.Qiugouxinxi;
import com.campus.mapper.QiugouxinxiMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/qiugouxinxi")
public class QiugouxinxiController {

    @Resource
    private QiugouxinxiMapper qiugouxinxiMapper;

    @GetMapping("/list")
    public Result<PageResult<Qiugouxinxi>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String keyword
    ) {
        Page<Qiugouxinxi> p = new Page<>(page, limit);
        LambdaQueryWrapper<Qiugouxinxi> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like(Qiugouxinxi::getShangpinmingcheng, keyword)
                    .or().like(Qiugouxinxi::getQiugoubianhao, keyword));
        }
        qw.orderByDesc(Qiugouxinxi::getAddtime);
        Page<Qiugouxinxi> result = qiugouxinxiMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Qiugouxinxi entity) {
        if (entity.getQiugoubianhao() == null || entity.getQiugoubianhao().isEmpty()) {
            entity.setQiugoubianhao(System.currentTimeMillis() + "");
        }
        if (entity.getQiugoushijian() == null) {
            entity.setQiugoushijian(LocalDate.now());
        }
        qiugouxinxiMapper.insert(entity);
        return Result.success("发布成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Qiugouxinxi entity) {
        qiugouxinxiMapper.updateById(entity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        qiugouxinxiMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
