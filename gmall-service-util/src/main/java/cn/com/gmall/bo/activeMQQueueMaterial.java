package cn.com.gmall.bo;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;

public class activeMQQueueMaterial extends activeMQMaterial {
    private QueueConnection queueConnection;
    private QueueSession session;

    public QueueConnection getConnection() {
        return queueConnection;
    }

    public void setQueueConnection(QueueConnection queueConnection) {
        this.queueConnection = queueConnection;
    }

    public QueueSession getSession() {
        return session;
    }

    public void setSession(QueueSession session) {
        this.session = session;
    }
}
