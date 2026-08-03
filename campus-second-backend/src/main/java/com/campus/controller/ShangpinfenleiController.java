package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.Result;
import com.campus.entity.Shangpinfenlei;
import com.campus.entity.Shangpinxinxi;
import com.campus.entity.Qiugouxinxi;
import com.campus.mapper.ShangpinfenleiMapper;
import com.campus.mapper.ShangpinxinxiMapper;
import com.campus.mapper.QiugouxinxiMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品分类接口
 */
@RestController
@RequestMapping("/api/shangpinfenlei")
public class ShangpinfenleiController {

    @Resource
    private ShangpinfenleiMapper shangpinfenleiMapper;
    @Resource
    private ShangpinxinxiMapper shangpinxinxiMapper;
    @Resource
    private QiugouxinxiMapper qiugouxinxiMapper;

    @GetMapping("/list")
    public Result<List<Shangpinfenlei>> list() {
        List<Shangpinfenlei> list = shangpinfenleiMapper.selectList(
                new LambdaQueryWrapper<Shangpinfenlei>().orderByAsc(Shangpinfenlei::getId)
        );
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Shangpinfenlei entity) {
        shangpinfenleiMapper.insert(entity);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Shangpinfenlei entity) {
        shangpinfenleiMapper.updateById(entity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Shangpinfenlei fenlei = shangpinfenleiMapper.selectById(id);
        if (fenlei == null) {
            return Result.error("分类不存在");
        }
        String fenleiName = fenlei.getShangpinfenlei();
        // 检查是否有商品使用该分类
        long goodsCount = shangpinxinxiMapper.selectCount(
                new LambdaQueryWrapper<Shangpinxinxi>().eq(Shangpinxinxi::getShangpinfenlei, fenleiName)
        );
        if (goodsCount > 0) {
            return Result.error("该分类下存在 " + goodsCount + " 个商品，无法删除。请先移除或转移这些商品后再删除分类。");
        }
        // 检查是否有求购信息使用该分类
        long qiugouCount = qiugouxinxiMapper.selectCount(
                new LambdaQueryWrapper<Qiugouxinxi>().eq(Qiugouxinxi::getShangpinfenlei, fenleiName)
        );
        if (qiugouCount > 0) {
            return Result.error("该分类下存在 " + qiugouCount + " 条求购信息，无法删除。请先处理这些求购信息后再删除分类。");
        }
        shangpinfenleiMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
