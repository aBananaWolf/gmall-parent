package cn.com.gmall.manage.config;

import cn.com.gmall.manage.service.impl.SkuServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManageServiceClass {
    @Bean("skuServiceImplClass")
    public Class<?> skuServiceImplClass() {
        return SkuServiceImpl.class;
    }
}
