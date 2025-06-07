package com.example.backend.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alipay")
@Data
@Slf4j
public class AlipayConfig {
    private String appId;
    private String merchantPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;

    @Bean
    public AlipayClient alipayClient() {

        String cleanedPrivateKey = cleanKey(merchantPrivateKey);
        String cleanedPublicKey = cleanKey(alipayPublicKey);

        log.info("应用私钥长度: {}", cleanedPrivateKey.length());
        log.info("支付宝公钥长度: {}", cleanedPublicKey.length());
        log.debug("支付宝公钥内容: {}", cleanedPublicKey);

        return new DefaultAlipayClient(
                "https://openapi-sandbox.dl.alipaydev.com/gateway.do",
                appId,
                cleanKey(merchantPrivateKey),
                "JSON",
                "UTF-8",
                cleanKey(alipayPublicKey),
                "RSA2"
        );
    }

    private String cleanKey(String key) {
        if (key == null) return "";
        return key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
    }
}
