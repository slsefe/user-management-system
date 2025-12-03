package com.zixi.usermanagementsystem.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zixi.usermanagementsystem.security.WebAuthenticationDetailsMixin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();

        // 关键：启用 Default Typing，写入 @class 字段
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 支持 Java 8 时间
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new CoreJackson2Module());
        // 手动注册 WebAuthenticationDetails 的 Mixin，用于反序列化WebAuthenticationDetails
        mapper.addMixIn(WebAuthenticationDetails.class, WebAuthenticationDetailsMixin.class);
        mapper.registerModule(new Jdk8Module());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 日期格式为 ISO8601 字符串

        System.out.println("Registered modules: " + mapper.getRegisteredModuleIds());
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}
