package com.zixi.usermanagementsystem.configuration;

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF（适用于 REST API）
                .csrf(csrf -> csrf.disable())

                // Session 策略：由 Spring Session 管理（Redis）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // 认证方式：表单登录，但自定义处理 URL
                .formLogin(form -> form
                        .loginProcessingUrl("/api/users/login")   // 登录接口
                        .usernameParameter("account")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            // 登录成功后，返回 JSON（不跳转）
                            response.setStatus(200);
                            response.getWriter().print("Login success");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.getWriter().print("Invalid credentials");
                        })
                )

                // 自定义登出
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")           // 登出接口
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.getWriter().print("Logged out");
                        })
                )

                // 授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll() // 允许注册和登录
                        .requestMatchers("/api/users/me").authenticated()
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
