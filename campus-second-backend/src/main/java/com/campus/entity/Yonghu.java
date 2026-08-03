package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
@TableName("yonghu")
public class Yonghu {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime addtime;
    private String yonghuzhanghao;
    private String mima;
    private String yonghuxingming;
    private String xingbie;
    private String lianxifangshi;
    private String touxiang;
    private Float money;
}
