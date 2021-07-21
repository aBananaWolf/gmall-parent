package cn.com.gmall.config;

import cn.com.gmall.util.LoginUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginConfig /*implements WebMvcConfigurer*/ {
/*    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**","/index")
                .excludePathPatterns("/error","/index/css/**","/index/img/**","/index/js/**","/index/json/**",
                        "/list/**","/bootstrap/**","/success.html",
                        "/css/**","/image/**","/img/**","/js/**");

    }*/

    @Bean
    public LoginUtil loginUtil() {
        return new LoginUtil();
    }


}
