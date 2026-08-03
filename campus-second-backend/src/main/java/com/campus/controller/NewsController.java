package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.Result;
import com.campus.dto.PageResult;
import com.campus.entity.News;
import com.campus.mapper.NewsMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 公告接口
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Resource
    private NewsMapper newsMapper;

    @GetMapping("/list")
    public Result<PageResult<News>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        Page<News> p = new Page<>(page, limit);
        LambdaQueryWrapper<News> qw = new LambdaQueryWrapper<News>().orderByDesc(News::getAddtime);
        Page<News> result = newsMapper.selectPage(p, qw);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @GetMapping("/{id}")
    public Result<News> detail(@PathVariable Long id) {
        News entity = newsMapper.selectById(id);
        if (entity == null) {
            return Result.error("公告不存在");
        }
        return Result.success(entity);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody News entity) {
        newsMapper.insert(entity);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody News entity) {
        newsMapper.updateById(entity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        newsMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
