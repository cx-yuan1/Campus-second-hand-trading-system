package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime addtime;
    private String tablename;
    private Long userid;
    private Long goodid;
    private String goodname;
    private String picture;
    private Integer buynumber;
    private Float price;
    private Float discountprice;
    private String yonghuzhanghao;
}
