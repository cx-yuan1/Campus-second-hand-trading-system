package com.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.campus.common.Result;
import com.campus.entity.Address;
import com.campus.mapper.AddressMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Resource
    private AddressMapper addressMapper;

    @GetMapping("/list")
    public Result<List<Address>> list(@RequestParam Long userid) {
        List<Address> list = addressMapper.selectList(
                new LambdaQueryWrapper<Address>().eq(Address::getUserid, userid)
        );
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Address entity) {
        if ("是".equals(entity.getIsdefault())) {
            addressMapper.update(null, new UpdateWrapper<Address>()
                    .eq("userid", entity.getUserid())
                    .set("isdefault", "否"));
        }
        addressMapper.insert(entity);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Address entity) {
        if ("是".equals(entity.getIsdefault())) {
            addressMapper.update(null, new UpdateWrapper<Address>()
                    .eq("userid", entity.getUserid())
                    .set("isdefault", "否"));
        }
        addressMapper.updateById(entity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, @RequestParam Long userid) {
        addressMapper.delete(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, id)
                .eq(Address::getUserid, userid));
        return Result.success("删除成功");
    }
}
