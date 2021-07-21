package cn.com.gmall.payment.config.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "alipay")
@PropertySource("classpath:alipay.properties")
public class AlipayConfig {
    private String alipay_url;
    private String app_id;
    private String app_private_key;
    private String alipay_public_key;
    // 同步回调地址 重定向地址本地浏览器
    private String return_payment_url;
    // 异步通知地址 公网接口(webService)
    private String notify_payment_url;
    private String return_order_url;
    private String product_code;

    private final String sign_type = "RSA2";
    private final String format = "json";
    private final String charset = "UTF-8";

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(alipay_url, app_id, app_private_key, format, charset, alipay_public_key, sign_type);
    }


    public void setAlipay_url(String alipay_url) {
        this.alipay_url = alipay_url;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setApp_private_key(String app_private_key) {
        this.app_private_key = app_private_key;
    }

    public void setAlipay_public_key(String alipay_public_key) {
        this.alipay_public_key = alipay_public_key;
    }

    public void setReturn_payment_url(String return_payment_url) {
        this.return_payment_url = return_payment_url;
    }

    public void setNotify_payment_url(String notify_payment_url) {
        this.notify_payment_url = notify_payment_url;
    }

    public void setReturn_order_url(String return_order_url) {
        this.return_order_url = return_order_url;
    }

    public String getAlipay_url() {
        return alipay_url;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getApp_private_key() {
        return app_private_key;
    }

    public String getAlipay_public_key() {
        return alipay_public_key;
    }

    public String getReturn_payment_url() {
        return return_payment_url;
    }

    public String getNotify_payment_url() {
        return notify_payment_url;
    }

    public String getReturn_order_url() {
        return return_order_url;
    }

    public String getSign_type() {
        return sign_type;
    }

    public String getFormat() {
        return format;
    }

    public String getCharset() {
        return charset;
    }
}
