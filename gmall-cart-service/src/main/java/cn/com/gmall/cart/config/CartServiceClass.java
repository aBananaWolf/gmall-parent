package cn.com.gmall.cart.config;

import cn.com.gmall.cart.service.impl.CartServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartServiceClass {

    @Bean("cartServiceImplClass")
    public Class<?> cartServiceClass() {
        return CartServiceImpl.class;
    }
}
