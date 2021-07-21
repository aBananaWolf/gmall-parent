package cn.com.gmall.user.controller;

import cn.com.gmall.util.JedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.sound.midi.Soundbank;
import java.util.Collections;
import java.util.UUID;

@RestController
public class MyTest {
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private RedissonClient redissonClient;

    /*  @RequestMapping("/redisson-test")//http://redis.gmall.com/redisson-test
      public String test (){
          Jedis jedis = jedisUtil.getJedis();
          RLock lock = redissonClient.getLock("lock");
          lock.lock();
          try {
              String k = jedis.get("k");
              if(k != null){
                  int i = Integer.parseInt(k);
                  i++;
                  System.out.println(k);
                  jedis.set("k",i+"");
              }else{
                  jedis.set("k",1+"");
              }
          } finally {
              jedis.close();
              lock.unlock();
          }
          return "success";
      }*/
    @RequestMapping("/redisson-test")//http://redis.gmall.com/redisson-test
    public String test() {
        Jedis jedis = jedisUtil.getJedis();
        String lockValue = UUID.randomUUID().toString();
        String lock = jedis.set("key", lockValue, "nx", "ex", 10);
        if ("OK".equals(lock)) {
            try {
                String k = jedis.get("k");
                if (k != null) {
                    int i = Integer.parseInt(k);
                    i++;
                    System.out.println(k);
                    jedis.set("k", i + "");
                } else {
                    jedis.set("k", 1 + "");
                }
                jedis.eval("if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end",
                        Collections.singletonList("key"), Collections.singletonList(lockValue));
            } finally {
                jedis.close();
            }
        } else {
            try {
                //                Thread.sleep(1000);
                //            } catch (InterruptedException e) {
                //                e.printStackTrace();
            } finally {
                jedis.close();
            }
            test();
        }
        return "success";
    }

    public static void main(String[] args) {
        MyTest myTest = new MyTest();
        System.out.println(myTest.a(1));
    }

    int a(int c) {
        System.out.println(Thread.currentThread().getId() + "开启资源");
        try {
            if (c <= 5) {
                c++;
                System.out.println(c);
                System.out.println("休眠");
                System.out.println("关闭资源");
                return a(c);
            }
        } finally {
            System.out.println(Thread.currentThread().getId() + "关闭资源");
        }
        return c;
    }
    /**
     *     public static void main(String[] args) {
     *         int gcd = gcd(12, 20);
     *         System.out.println(gcd);
     *     }
     *
     *     private static int gcd(int a, int b) {
     *         if (b != 0) {
     *             int i = a % b;
     *             a = b;
     *             b = i;
     *
     *            return  gcd(a, b);      // 加不加return是两种结果，如果加了return，那么不会开启新线程
     *         }
     *         return a;
     *     }
     */
}
