package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("storeup")
public class Storeup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime addtime;
    private Long userid;
    private Long refid;
    private String tablename;
    private String name;
    private String picture;
    private String type;
    private String inteltype;
    private String remark;
}
