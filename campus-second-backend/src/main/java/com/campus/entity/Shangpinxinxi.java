package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品信息
 */
@Data
@TableName("shangpinxinxi")
public class Shangpinxinxi {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime addtime;
    private String shangpinbianhao;
    private String shangpinmingcheng;
    private String shangpinfenlei;
    private String guige;
    private String tupian;
    private String xinjiuchengdu;
    private LocalDate fabushijian;
    private String shangpinxiangqing;
    private String yonghuzhanghao;
    private String yonghuxingming;
    private Integer onelimittimes;
    private Integer alllimittimes;
    private LocalDateTime clicktime;
    private Integer clicknum;
    private Float price;
}
