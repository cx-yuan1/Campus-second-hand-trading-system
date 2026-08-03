package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime addtime;
    private String orderid;
    private String tablename;
    private Long userid;
    private Long goodid;
    private String goodname;
    private String picture;
    private Integer buynumber;
    private Float price;
    private Float discountprice;
    private Float total;
    private Float discounttotal;
    private Integer type;
    private String status;
    private String address;
    private String tel;
    private String consignee;
    private String remark;
    private String logistics;
    private String yonghuzhanghao;
}
