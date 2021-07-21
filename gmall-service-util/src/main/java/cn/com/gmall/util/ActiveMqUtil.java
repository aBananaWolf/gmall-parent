package cn.com.gmall.util;

import cn.com.gmall.bo.activeMQMaterial;
import cn.com.gmall.bo.activeMQQueueMaterial;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.jms.*;

public class ActiveMqUtil {
    private PooledConnectionFactory pooledConnectionFactory = null;

    public void init(String brokerUrl) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD, brokerUrl);
        pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
        pooledConnectionFactory.setReconnectOnException(true);
        pooledConnectionFactory.setExpiryTimeout(10000);
        pooledConnectionFactory.setMaxConnections(5);
    }

    public PooledConnectionFactory getConnect() {
        return pooledConnectionFactory;
    }


    public activeMQQueueMaterial getQueueMaterial() {
        activeMQQueueMaterial activeMQQueueMaterial = new activeMQQueueMaterial();
        try {
            QueueConnection queueConnection = pooledConnectionFactory.createQueueConnection();
            activeMQQueueMaterial.setQueueConnection(queueConnection);
            activeMQQueueMaterial.setSession(queueConnection.createQueueSession(true, Session.SESSION_TRANSACTED));
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return activeMQQueueMaterial;
    }

    public void commitQueue(activeMQQueueMaterial base, String queueStr, Message message) throws JMSException {
        if (base != null) {
            Queue queue = base.getSession().createQueue(queueStr);
            MessageProducer producer = base.getSession().createProducer(queue);
            producer.send(message);
            base.getSession().commit();
        }
    }

    /**
     * 作为消费者的回滚，如果失败则抛出异常，重新加入队列
     *
     * @param connect
     */
    public void consumerRollBack(activeMQMaterial connect) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        if (connect != null) {
            Session session = connect.getSession();
            if (session != null) {
                try {
                    session.rollback();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new RuntimeException();
    }

    /**
     * 发送者通用回滚
     *
     * @param connect
     */
    public void producerRollBack(activeMQMaterial connect) {
        if (connect != null) {
            Session session = connect.getSession();
            if (session != null) {
                try {
                    session.rollback();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close(activeMQMaterial connect) {
        if (connect != null) {
            Session session = connect.getSession();
            Connection queueConnection = connect.getConnection();
            try {
                if (session != null)
                    session.close();
                if (queueConnection != null)
                    queueConnection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendQueue(String queueName, Message message) {
        activeMQQueueMaterial queueMaterial = null;
        try {
            queueMaterial = this.getQueueMaterial();
            this.commitQueue(queueMaterial, queueName, message);
        } catch (Exception e) {
            this.producerRollBack(queueMaterial);
        } finally {
            this.close(queueMaterial);
        }
    }
}
