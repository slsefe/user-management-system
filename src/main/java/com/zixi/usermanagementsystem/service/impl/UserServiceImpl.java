package com.zixi.usermanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.controller.dto.UserRegisterDTO;
import com.zixi.usermanagementsystem.domain.User;
import com.zixi.usermanagementsystem.service.UserService;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author baiyin
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-10-13 19:43:57
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    @Override
    public Long register(UserRegisterDTO userRegisterDTO) {
        // 用户账号校验：不能和已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", userRegisterDTO.getAccount());
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1L;
        }

        // 密码使用md5加密后存储
        final String salt = UUID.randomUUID().toString().replace("-", "");
        String password = DigestUtils.md5DigestAsHex((salt + userRegisterDTO.getPassword()).getBytes());
        // 保存到数据库
        User user = new User();
        user.setAccount(userRegisterDTO.getAccount());
        user.setPassword(password);
        int inserted = userMapper.insert(user);
        if (inserted == 0) {
            return -1L;
        }
        return user.getId();
    }
}




