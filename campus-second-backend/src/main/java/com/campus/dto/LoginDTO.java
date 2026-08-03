package com.campus.dto;

import lombok.Data;

/**
 * 登录请求
 */
@Data
public class LoginDTO {
    private String username;   // 管理员用 username，用户用 yonghuzhanghao
    private String password;
    private String role;       // admin / user
}
