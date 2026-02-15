package com.zixi.usermanagementsystem.service;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import com.zixi.usermanagementsystem.configuration.TencentSmsProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 腾讯云短信服务
 * 提供真实的短信发送能力
 */
@Slf4j
@Service
public class TencentSmsService {

    @Resource
    private TencentSmsProperties tencentSmsProperties;

    /**
     * 发送短信验证码
     * @param phoneNumber 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    public Boolean sendVerificationCode(String phoneNumber, String code) {
        // 如果未启用短信服务，直接返回成功（打印日志）
        if (!Boolean.TRUE.equals(tencentSmsProperties.getEnabled())) {
            log.info("【短信服务未启用】手机号: {}, 验证码: {}", phoneNumber, code);
            return true;
        }

        try {
            // 实例化认证对象
            Credential cred = new Credential(
                    tencentSmsProperties.getSecretId(),
                    tencentSmsProperties.getSecretKey()
            );

            // 实例化 HTTP 选项
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            // 实例化客户端选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化 SMS 客户端
            SmsClient client = new SmsClient(cred, tencentSmsProperties.getRegion(), clientProfile);

            // 实例化请求对象
            SendSmsRequest req = new SendSmsRequest();

            // 设置手机号（需要+86前缀）
            String[] phoneNumberSet = {"+86" + phoneNumber};
            req.setPhoneNumberSet(phoneNumberSet);

            // 设置短信应用 SDK AppID
            req.setSmsSdkAppId(tencentSmsProperties.getAppId());

            // 设置短信签名
            req.setSignName(tencentSmsProperties.getSignName());

            // 设置短信模板 ID
            req.setTemplateId(tencentSmsProperties.getTemplateId());

            // 设置模板参数（验证码）
            String[] templateParamSet = {code};
            req.setTemplateParamSet(templateParamSet);

            // 发送短信
            SendSmsResponse response = client.SendSms(req);

            // 处理响应
            SendStatus[] sendStatusSet = response.getSendStatusSet();
            if (sendStatusSet != null && sendStatusSet.length > 0) {
                SendStatus status = sendStatusSet[0];
                if ("Ok".equals(status.getCode())) {
                    log.info("短信发送成功: phone={}, serialNo={}", phoneNumber, status.getSerialNo());
                    return true;
                } else {
                    log.error("短信发送失败: phone={}, code={}, message={}",
                            phoneNumber, status.getCode(), status.getMessage());
                    return false;
                }
            }

            return false;

        } catch (TencentCloudSDKException e) {
            log.error("腾讯云短信 SDK 异常: phone={}, error={}", phoneNumber, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("短信发送异常: phone={}, error={}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }
}
