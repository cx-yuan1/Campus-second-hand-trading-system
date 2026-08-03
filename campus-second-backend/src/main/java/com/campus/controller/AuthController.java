package com.campus.controller;

import com.campus.common.Result;
import com.campus.dto.LoginDTO;
import com.campus.dto.LoginVO;
import com.campus.service.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 登录接口
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 管理员登录
     */
    @PostMapping("/admin/login")
    public Result<LoginVO> adminLogin(@RequestBody LoginDTO dto) {
        LoginVO vo = authService.adminLogin(dto.getUsername(), dto.getPassword());
        if (vo == null) {
            return Result.error("账号或密码错误");
        }
        return Result.success(vo);
    }

    /**
     * 用户登录
     */
    @PostMapping("/user/login")
    public Result<LoginVO> userLogin(@RequestBody LoginDTO dto) {
        LoginVO vo = authService.userLogin(dto.getUsername(), dto.getPassword());
        if (vo == null) {
            return Result.error("账号或密码错误");
        }
        return Result.success(vo);
    }
}
