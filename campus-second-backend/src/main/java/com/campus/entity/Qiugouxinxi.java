package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("qiugouxinxi")
public class Qiugouxinxi {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime addtime;
    private String qiugoubianhao;
    private String shangpinmingcheng;
    private String shangpinfenlei;
    private String guige;
    private String tupian;
    private Float qiugoujiage;
    private LocalDate qiugoushijian;
    private String yonghuzhanghao;
    private String yonghuxingming;
    private String qiugouxiangqing;
}
