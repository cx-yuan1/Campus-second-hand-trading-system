package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("juanzengxinxi")
public class Juanzengxinxi {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime addtime;
    private String juanzengbianhao;
    private String shangpinmingcheng;
    private String tupian;
    private Integer juanzengshuliang;
    private LocalDate juanzengshijian;
    private String juanzengdizhi;
    private String yonghuzhanghao;
    private String yonghuxingming;
    private String lianxifangshi;
    private String beizhu;
}
