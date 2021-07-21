package cn.com.gmall.config;

import cn.com.gmall.bo.activeMQQueueMaterial;
import cn.com.gmall.constants.SecurityCacheUnit;
import cn.com.gmall.exception.SecurityCacheException;
import cn.com.gmall.anno.ActiveMQSecurityCache;
import cn.com.gmall.util.ActiveMqUtil;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class SecurityCacheAspect {
    @Autowired
    private ActiveMqUtil activeMqUtil;

    @Pointcut(value = "@annotation(cn.com.gmall.anno.ActiveMQSecurityCache)")
    public void securityCachePointcut() {
    }

    @Around(value = "securityCachePointcut()")
    // 不能因为缓存更新失败就回滚数据库，aop可以获取目标对象以及参数，操作流是危险操作，封装参数进入mq √
    // 消息是绕着服务走的 √
    // 不能封装流，那么就不应该将对象信息传入 √
    // mq的aspect应该有队列名和json参数 √
    public Object securityCacheAspect(ProceedingJoinPoint joinPoint) {
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            this.sendMessage(joinPoint, SecurityCacheUnit.RESEND_CACHE_MESSAGE);
        }
        return result;
    }

    public void sendMessage(ProceedingJoinPoint joinPoint, int count) {
        if (count <= 0) {
            throw new SecurityCacheException("超过最大重试，抛出异常");
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ActiveMQSecurityCache annotation = method.getAnnotation(ActiveMQSecurityCache.class);
        // 序列化值
        int paramIndex = annotation.becomeMainParamOfJson();
        Object[] args = joinPoint.getArgs();
        String paramJson = JSON.toJSONString(args[paramIndex]);

        // 消息名
        String messageName = annotation.messageName();
        // 队列名
        String queueName = annotation.queueName();
        if (!"".equals(queueName)) {
            activeMQQueueMaterial queueMaterial = null;
            try {
                queueMaterial = activeMqUtil.getQueueMaterial();
                ActiveMQMapMessage message = new ActiveMQMapMessage();
                message.setString(messageName, paramJson);
                activeMqUtil.commitQueue(queueMaterial, queueName, message);
            } catch (Exception e) {
                activeMqUtil.producerRollBack(queueMaterial);
                count--;
                this.sendMessage(joinPoint, count);
            } finally {
                activeMqUtil.close(queueMaterial);
            }
        }
    }
}
