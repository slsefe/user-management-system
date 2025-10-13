package com.zixi.usermanagementsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.controller.dto.UserRegisterDTO;
import com.zixi.usermanagementsystem.domain.User;
import com.zixi.usermanagementsystem.service.UserService;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author baiyin
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-10-13 19:43:57
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    public Long register(UserRegisterDTO userRegisterDTO) {
        // TODO: 用户账号校验：不能和已有的重复
        // TODO：密码使用SHA256或者BASE64加密存储
        // TODO: 保存到数据库
        return 0L;
    }
}




