package cn.com.gmall.config;

import cn.com.gmall.util.JedisUtil;
import cn.com.gmall.util.RedissonUtil;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@ConfigurationProperties("jedis-util")
public class RedisConfig {
    private String host;
    private int port;
    private int timeOut;
    private String password;
    private int maxWaitMillis;
    private int maxIdle;
    private int maxTotal;
    private boolean testOnReturn;
    private boolean testOnBorrow;
    private boolean testWhileIdle;
    private boolean blockWhenExhausted;

    @Bean
    public JedisUtil jedisUtil() {
        JedisUtil jedisUtil = new JedisUtil();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);
        config.setTestOnReturn(testOnReturn);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestWhileIdle(testWhileIdle);
        config.setBlockWhenExhausted(blockWhenExhausted);
        jedisUtil.init(config, host, port, timeOut, password);
        return jedisUtil;
    }

    @Bean
    public RedissonClient redissonClient() {
        RedissonUtil redissonUtil = new RedissonUtil();
        return redissonUtil.init(host, port + "");
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
