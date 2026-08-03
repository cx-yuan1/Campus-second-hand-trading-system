package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类
 */
@Data
@TableName("shangpinfenlei")
public class Shangpinfenlei {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime addtime;
    private String shangpinfenlei;
}
