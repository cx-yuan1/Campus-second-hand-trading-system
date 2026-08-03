package com.campus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录返回
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    private Long id;
    private String username;   // 管理员 username，用户 yonghuzhanghao
    private String role;
    private String token;
}
