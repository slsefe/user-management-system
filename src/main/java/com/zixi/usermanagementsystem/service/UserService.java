package com.zixi.usermanagementsystem.service;

import com.zixi.usermanagementsystem.controller.dto.UserRegisterDTO;
import com.zixi.usermanagementsystem.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author baiyin
* @description 针对表【user】的数据库操作Service
* @createDate 2025-10-13 19:43:57
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return 用户注册的账号id
     */
    Long register(UserRegisterDTO userRegisterDTO);

}
