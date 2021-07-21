package cn.com.gmall.controller;

import cn.com.gmall.util.JedisUtil;
import cn.com.gmall.util.RedissonUtil;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

@RestController
public class SeckillController {
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private RedissonClient redissonClient;

    @RequestMapping("/redisson")
    public void redisson() throws InterruptedException {
        RSemaphore semaphore = redissonClient.getSemaphore("product");
        boolean b = semaphore.tryAcquire();
        semaphore.acquire();
        if (b) {
            try (Jedis jedis = jedisUtil.getJedis()) {
                if (jedis != null) {
                    System.out.println(jedis.incr("key"));
                }
            }
        }
        semaphore.release();
    }

    @RequestMapping("/seckill")
    // localhost:8013/seckill
    public void seckill() {
        try (Jedis jedis = jedisUtil.getJedis()) {

            if (jedis != null) {
                jedis.watch("product");
                String product = jedis.get("product");
                int num = Integer.parseInt(String.valueOf(product));
                if (num > 0) {
                    Transaction tx = jedis.multi();
                    tx.decr("product");
                    List<Object> exec = tx.exec();
                    if (exec != null && exec.size() > 0) {
                        System.out.println("系统剩余商品：" + (num - 1) + "  到货人数：" + (100 - Integer.parseInt(String.valueOf(exec.get(0)))));
                    } else {
                        System.out.println("没有抢到商品: " + (num - 1));
                    }
                }
            }
        }
    }
}
