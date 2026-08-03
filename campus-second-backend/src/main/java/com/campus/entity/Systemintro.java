package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统简介
 */
@Data
@TableName("systemintro")
public class Systemintro {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime addtime;
    private String title;
    private String subtitle;
    private String content;
    private String picture1;
    private String picture2;
    private String picture3;
}
