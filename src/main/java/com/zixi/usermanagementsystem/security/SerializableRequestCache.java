package com.zixi.usermanagementsystem.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Spring Security 在用户未登录时访问受保护资源，会将原始请求保存为 SavedRequest（通常是 DefaultSavedRequest 实例），以便登录后跳转回去。
 * 如果你使用的是 Spring Session + Redis，并且配置了 JSON 序列化（而不是 JDK 原生序列化），那么这些对象会被尝试用 Jackson 反序列化。
 * 但 DefaultSavedRequest 是一个内部类，没有无参构造函数，也不支持 Jackson 反序列化，导致失败。
 *
 * 解决方案：自定义序列化策略，排除或转换 SavedRequest，自定义 RequestCache，不保存复杂对象，只保存必要信息（如 URL）
 */
@Component
public class SerializableRequestCache implements RequestCache {

    private final Logger logger = LoggerFactory.getLogger(SerializableRequestCache.class);

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        // 只保存原始请求 URL 到 session
        String uri = request.getRequestURI();
        if (request.getQueryString() != null) {
            uri += "?" + request.getQueryString();
        }
        request.getSession().setAttribute("SPRING_SECURITY_SAVED_REQUEST", uri);
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
        Object saved = request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (saved instanceof String) {
            try {
                // 构造一个简单的 SavedRequest（仅用于跳转）
                return new SimpleSavedRequest((String) saved);
            } catch (Exception e) {
                logger.warn("Failed to reconstruct saved request", e);
            }
        }
        return null;
    }

    @Override
    public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
        return getRequest(request, response) != null ? request : null;
    }

    @Override
    public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
    }

    // 简单的可序列化 SavedRequest 实现
    public static class SimpleSavedRequest implements SavedRequest, Serializable {
        private final String redirectUrl;

        public SimpleSavedRequest(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        @Override
        public String getRedirectUrl() {
            return redirectUrl;
        }

        // 其他方法返回默认值或抛出 UnsupportedOperationException
        @Override
        public List<String> getHeaderValues(String name) {
            return Collections.emptyList();
        }

        @Override
        public Collection<String> getHeaderNames() {
            return Collections.emptyList();
        }

        @Override
        public List<Locale> getLocales() {
            return List.of();
        }

        @Override
        public String[] getParameterValues(String name) {
            return new String[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Map.of();
        }

        @Override
        public String getMethod() {
            return "GET";
        }


        @Override
        public List<Cookie> getCookies() {
            return Collections.emptyList();
        }
    }
}
