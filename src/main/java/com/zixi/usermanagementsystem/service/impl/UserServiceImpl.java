package com.zixi.usermanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.constant.UserConstant;
import com.zixi.usermanagementsystem.model.request.UserLoginRequest;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.service.UserService;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author baiyin
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-10-13 19:43:57
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    private static final String SALT = "zixi";

    @Override
    public Long register(UserRegisterRequest userRegisterRequest) {
        // 用户账号校验：不能和已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", userRegisterRequest.getAccount());
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            // TODO: 统一返回编码
            return -1L;
        }

        // 密码使用md5加密后存储，保存到数据库
        User user = new User();
        user.setAccount(userRegisterRequest.getAccount());
        user.setPassword(encryptPassword(userRegisterRequest.getPassword()));
        int inserted = userMapper.insert(user);
        if (inserted == 0) {
            return -1L;
        }
        return user.getId();
    }

    @Override
    public User login(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", userLoginRequest.getAccount());
        queryWrapper.eq("password", encryptPassword(userLoginRequest.getPassword()));
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("account or password is wrong");
            return null;
        }

        User userVO = user.buildUserVO();

        // 记录用户的登录状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, userVO);

        return userVO;
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            return null;
        }
        User currentUser = userMapper.selectById(user.getId());
        return currentUser.buildUserVO();
    }

    private static String encryptPassword(String userRegisterRequest) {
        return DigestUtils.md5DigestAsHex((userRegisterRequest + SALT).getBytes());
    }

    @Override
    public List<User> queryUserList() {
        return this.list().stream().map(User::buildUserVO).collect(Collectors.toList());
    }
}
