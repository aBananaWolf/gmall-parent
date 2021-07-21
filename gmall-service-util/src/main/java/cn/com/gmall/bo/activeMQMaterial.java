package cn.com.gmall.bo;

import javax.jms.Connection;
import javax.jms.Session;

public abstract class activeMQMaterial {
    public abstract Connection getConnection();

    public abstract Session getSession();
}
