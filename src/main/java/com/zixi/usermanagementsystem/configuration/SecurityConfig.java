package com.zixi.usermanagementsystem.configuration;

import com.zixi.usermanagementsystem.security.CustomAuthenticationFailureHandler;
import com.zixi.usermanagementsystem.security.CustomAuthenticationSuccessHandler;
import com.zixi.usermanagementsystem.security.SerializableRequestCache;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private SerializableRequestCache requestCache;

    @Resource
    private CustomAuthenticationSuccessHandler successHandler;

    @Resource
    private CustomAuthenticationFailureHandler failureHandler;

    public SecurityConfig(SerializableRequestCache requestCache,
                          CustomAuthenticationSuccessHandler successHandler,
                          CustomAuthenticationFailureHandler failureHandler) {
        this.requestCache = requestCache;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF（适用于 REST API）
                .csrf(csrf -> csrf.disable())

                // Session 策略：由 Spring Session 管理（Redis）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .requestCache(cache -> cache.requestCache(requestCache))

                // 认证方式：表单登录，但自定义处理 URL
                .formLogin(form -> form
                        .loginProcessingUrl("/api/users/login")   // 登录接口
                        .usernameParameter("account")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )

                // 自定义登出
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")// 登出接口
                        .deleteCookies("SESSION")
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.getWriter().print("Logged out");
                        })
                )

                // 授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll() // 允许注册和登录
                        .requestMatchers("/api/users/current").authenticated()
                        .anyRequest().authenticated()       // 其他请求需登录
                );

        return http.build();
    }

    // 密码编码器（生产环境必须用 BCrypt）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
