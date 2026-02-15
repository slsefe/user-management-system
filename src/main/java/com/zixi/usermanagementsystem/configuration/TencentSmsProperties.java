package com.zixi.usermanagementsystem.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云短信配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "tencent.sms")
public class TencentSmsProperties {

    /**
     * 是否启用短信发送
     */
    private Boolean enabled = false;

    /**
     * 腾讯云 SecretId
     */
    private String secretId;

    /**
     * 腾讯云 SecretKey
     */
    private String secretKey;

    /**
     * 短信应用 SDK AppID
     */
    private String appId;

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板 ID
     */
    private String templateId;

    /**
     * 地域，默认 ap-guangzhou
     */
    private String region = "ap-guangzhou";
}
