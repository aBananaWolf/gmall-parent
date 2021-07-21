package cn.com.gmall.config;

import cn.com.gmall.constants.MQUnit;
import cn.com.gmall.util.ActiveMqUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.*;

@Configuration
public class ActiveMqConfig {
    @Value("${spring.activemq.broker-url:disable}")
    private String brokerUrl;
    @Value("${activemq.listener-enable:false}")
    private Boolean listenerEnable;

    @Bean("activeMqUtil")
    public ActiveMqUtil activeMqUtil() {
        if ("disable".equals(brokerUrl)) {
            return new ActiveMqUtil();
        }
        ActiveMqUtil activeMqUtil = new ActiveMqUtil();
        activeMqUtil.init(brokerUrl);
        return activeMqUtil;
    }

    @Bean(MQUnit.CONTAINER_FACTORY_NAME)
    public DefaultJmsListenerContainerFactory jmsQueueFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        if (!listenerEnable) {
            return null;
        }
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory);
        factory.setReceiveTimeout(5000L);
        // 并发数
        factory.setConcurrency("5");
        // 自动事务
        factory.setSessionTransacted(false);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        //        factory.setPubSubDomain(false);
        return factory;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        return new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD, brokerUrl);
    }

}
