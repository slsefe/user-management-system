package com.zixi.usermanagementsystem.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class WebAuthenticationDetailsMixin {

    @JsonCreator
    public WebAuthenticationDetailsMixin(
            @JsonProperty("remoteAddress") String remoteAddress,
            @JsonProperty("sessionId") String sessionId) {
        // Mixin 只用于指导反序列化，不需要实现逻辑
    }
}
