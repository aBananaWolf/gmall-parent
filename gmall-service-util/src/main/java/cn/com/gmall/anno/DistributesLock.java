package cn.com.gmall.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributesLock {

    // 指定缓存key，默认不启用
    String specifyCacheKey() default "";

    String cachePrefix();

    String cacheSuffix();

    // 是否将key序列化作为缓存key，特殊需求
    boolean serializableKey() default false;

    boolean pairKey() default false;
}
