package cn.com.gmall.cart.config;

import cn.com.gmall.cart.interceptor.ToTradeInterceptor;
import cn.com.gmall.interceptor.LoginStrongCheckAuthInterceptor;
import cn.com.gmall.interceptor.LoginWeakCheckAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginWeakCheckAuthInterceptor weakAuthInterceptor;
/*    @Autowired
    private ToTradeInterceptor toTradeInterceptor;*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(weakAuthInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/success.html", "/toTrade", "/error", "/bootstrap/**", "/css/**", "/image/**", "/img/**", "/js/**");
        /*registry.addInterceptor(toTradeInterceptor).addPathPatterns("/toTrade");*/
    }
}
