package cn.com.gmall.anno;


import cn.com.gmall.constants.SecurityCacheUnit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ActiveMQSecurityCache {

    String queueName() default "";

    int becomeMainParamOfJson() default SecurityCacheUnit.THE_FIRST_IS_MAIN_PARAM;

    String messageName();

    // 允许根据Key 查询缓存再进行发送消息, 暂时没有写这个功能
    boolean getCache() default false;

    String cacheKey() default "";
}
