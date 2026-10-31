package com.youkeda.shede.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 支付宝配置，值都从 properties 里读
@Configuration
public class AlipayConfig {

    // @Value 对应 properties 里的 key
    @Value("${alipay.app-id}")
    private String appId;

    // 应用私钥，只在服务端待着
    @Value("${alipay.private-key}")
    private String privateKey;

    // 支付宝公钥，验签靠它
    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    // 网关，沙箱和线上不是同一个
    @Value("${alipay.gateway-url}")
    private String gatewayUrl;

    // 异步通知地址，支付结果只认这个
    @Value("${alipay.notify-url}")
    private String notifyUrl;

    // 同步跳回的页面，只是给用户看看
    @Value("${alipay.return-url}")
    private String returnUrl;

    @Value("${alipay.charset:UTF-8}")
    private String charset;

    // 签名和验签要用同一个类型
    @Value("${alipay.sign-type:RSA2}")
    private String signType;

    @Value("${alipay.format:json}")
    private String format;

    // 参数顺序固定，写错不会立刻报错，验签时才炸
    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(gatewayUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
    }

    // 下面都是给 PayServiceImpl 用的 getter

    public String getAppId() {
        return appId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getCharset() {
        return charset;
    }

    public String getSignType() {
        return signType;
    }

    public String getFormat() {
        return format;
    }
}
