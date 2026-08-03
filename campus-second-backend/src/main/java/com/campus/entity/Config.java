package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 配置表（轮播图等）
 */
@Data
@TableName("config")
public class Config {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String value;
}
