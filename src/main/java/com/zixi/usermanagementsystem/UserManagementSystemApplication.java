package com.zixi.usermanagementsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
@MapperScan("com.zixi.usermanagementsystem.mapper")
public class UserManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementSystemApplication.class, args);
    }

}
