package cn.com.gmall.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * 废弃，springBoot2.0的拦截器不再提供反射方法，因此废弃此注解 2020/2/23
 */
public @interface AuthorityLogin {
    boolean strongCheck() default true;
}
