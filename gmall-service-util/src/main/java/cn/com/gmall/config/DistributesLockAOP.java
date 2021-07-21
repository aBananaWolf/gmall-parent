package cn.com.gmall.config;

import cn.com.gmall.anno.DistributesLock;
import cn.com.gmall.util.CacheUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Aspect
@Component
@EnableAspectJAutoProxy
public class DistributesLockAOP {
    @Autowired
    private CacheUtil cacheUtil;

    @Pointcut(value = "@annotation(cn.com.gmall.anno.DistributesLock)")
    public void distributesLockAnnotation() {
    }

    @Around("distributesLockAnnotation()")
    public Object distributesLockAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        // 强转后获取Method
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method originMethod = methodSignature.getMethod();
        originMethod.setAccessible(true);
        System.out.println(originMethod + "aop：gmall-service-util： package cn.com.gmall.config.DistributesLockAOP | anno: cn.com.gmall.anno.DistributesLock");

        // 获取源方法的注解
        DistributesLock annotation = originMethod.getAnnotation(DistributesLock.class);
        if (annotation != null) {
            // 是否指定key，默认空串
            String specifyCacheKey = annotation.specifyCacheKey();
            if (specifyCacheKey.equals("")) {
                specifyCacheKey = null;
            }
            // 获取注解里的ServiceImpl Class
            Class<?> instanceClass = joinPoint.getThis().getClass();
            // 实例化
            Object instance = instanceClass.newInstance();
            // 前缀
            String prefix = annotation.cachePrefix();
            // 后缀
            String suffix = annotation.cacheSuffix();
            // 参数
            Object[] params = joinPoint.getArgs();
            // 是否序列化key，默认false
            boolean serializableKey = annotation.serializableKey();
            // 是否将前两个参数作为key，默认false
            boolean pairKey = annotation.pairKey();
            Boolean isReturnList = null;
            Class<?> returnClass = null;
            Type type = originMethod.getGenericReturnType();
            // 如果带有泛型
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                // 获取参数的类型
                System.out.println(parameterizedType.getRawType());
                // 获取参数的泛型列表
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                // 实例化泛型为class
                returnClass = Class.forName(actualTypeArguments[0].getTypeName());
                // 有泛型一定是List返回值
                isReturnList = true;
            } else {
                // 没有带泛型
                returnClass = originMethod.getReturnType();
                // 没有泛型一定是Object
                isReturnList = false;
            }
            // 这里可以是Object,但是内部一定要根据泛型和是否List来转换
            return cacheUtil.accessCacheByAOP(returnClass, isReturnList, pairKey, specifyCacheKey, instance, prefix, suffix, joinPoint, serializableKey, params);
        }
        return joinPoint.proceed();
    }

}
