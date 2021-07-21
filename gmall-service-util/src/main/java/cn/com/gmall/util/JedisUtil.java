package cn.com.gmall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtil {
    private JedisPool jedispool;

    public void init(JedisPoolConfig config, String host, int port, int timeOut, String password) {
        jedispool = new JedisPool(config, host, port, timeOut, "".equals(password) ? null : password);
    }

    public Jedis getJedis() {
        return jedispool.getResource();
    }
}
