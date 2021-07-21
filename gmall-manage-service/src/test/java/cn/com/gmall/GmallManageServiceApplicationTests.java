package cn.com.gmall;

import cn.com.gmall.util.JedisUtil;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;


@SpringBootTest
class GmallManageServiceApplicationTests {
    @Autowired
    private JedisUtil jedisUtil;

    @Test
    void contextLoads() {
        System.out.println(jedisUtil);
        System.out.println(jedisUtil.getJedis());
        Jedis jedis = jedisUtil.getJedis();
        jedis.set("key", "value");
        System.out.println(jedis);
    }
}
