package com.zixi.usermanagementsystem.service;

import com.zixi.usermanagementsystem.model.request.UserLoginRequest;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author baiyin
* @description 针对表【user】的数据库操作Service
* @createDate 2025-10-13 19:43:57
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return 用户注册的账号id
     */
    Long register(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     * @param userLoginRequest
     * @return 如果账号密码正确，返回用户信息；如果账号不存在或者密码错误，返回null
     */
    User login(UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest);

    /**
     * 返回全部用户列表
     * @return 用户列表
     */
    List<User> queryUserList();

    /**
     * 获取当前用户
     * @param request
     * @return 返回当前用户信息
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 退出登录
     * @param request
     * @return
     */
    void logout(HttpServletRequest request);
}
