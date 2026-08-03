package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.dto.LoginVO;
import com.campus.entity.Users;
import com.campus.entity.Yonghu;
import com.campus.mapper.UsersMapper;
import com.campus.mapper.YonghuMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 登录认证服务（明文校验）
 */
@Service
public class AuthService {

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private YonghuMapper yonghuMapper;

    /**
     * 管理员登录
     */
    public LoginVO adminLogin(String username, String password) {
        Users user = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getUsername, username)
        );
        if (user == null) {
            return null;
        }
        if (!password.equals(user.getPassword())) {
            return null;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        return new LoginVO(user.getId(), user.getUsername(), "管理员", token);
    }

    /**
     * 用户登录
     */
    public LoginVO userLogin(String username, String password) {
        Yonghu yonghu = yonghuMapper.selectOne(
                new LambdaQueryWrapper<Yonghu>()
                        .eq(Yonghu::getYonghuzhanghao, username)
        );
        if (yonghu == null) {
            return null;
        }
        if (!password.equals(yonghu.getMima())) {
            return null;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        return new LoginVO(yonghu.getId(), yonghu.getYonghuzhanghao(), "用户", token);
    }
}
