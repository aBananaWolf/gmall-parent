package cn.com.gmall.payment;

import cn.com.gmall.util.ActiveMqUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.messaging.MessageHeaders;

import javax.jms.*;
import java.io.IOException;

@SpringBootTest
class GmallPaymentApplicationTests {
    @Autowired
    private ActiveMqUtil activeMqUtil;
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    /*

    @Autowired
    @Qualifier("queueListener")
    private DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory;*/

    private static void onMessage(Message message) {
        try {
            System.out.println(((TextMessage) message).getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    @JmsListener(destination = "test-2", containerFactory = "queueListener")
    void consumer(String message) {
        System.out.println(message + "带上了防化服，邀请你去吃热干面");
    }

    @Test
    void producer() throws JMSException {
        Queue activeMQQueue = new ActiveMQQueue("test-2");
        jmsMessagingTemplate.convertAndSend(activeMQQueue, "你的好友小白");
/*        ActiveMQConnectionFactory connect = activeMqUtil.getConnect();
        QueueConnection queueConnection = connect.createQueueConnection();
        QueueSession session = queueConnection.createQueueSession(true, Session.SESSION_TRANSACTED);
        Queue queue = session.createQueue("test-2");
        QueueSender sender = session.createSender(queue);
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("你的好友小白");
        sender.send(activeMQTextMessage);
        session.commit();
        session.close();
        queueConnection.close();*/
    }

}
