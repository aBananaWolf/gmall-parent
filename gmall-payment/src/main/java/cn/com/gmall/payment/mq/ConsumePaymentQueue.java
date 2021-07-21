package cn.com.gmall.payment.mq;

import cn.com.gmall.beans.PaymentInfo;
import cn.com.gmall.constants.CacheUnit;
import cn.com.gmall.constants.MQUnit;
import cn.com.gmall.constants.TradeMQUnit;
import cn.com.gmall.payment.constants.PaymentUnit;
import cn.com.gmall.service.payment.PaymentService;
import cn.com.gmall.util.JedisUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class ConsumePaymentQueue {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private JedisUtil jedisUtil;

    @JmsListener(destination = PaymentUnit.DELAY_PAYMENT_CHECK_QUEUE, containerFactory = MQUnit.CONTAINER_FACTORY_NAME)
    public void consumeDelayPaymentCheckQueue(MapMessage mapMessage) {
        this.checkPaymentResult(mapMessage);
    }

    @JmsListener(destination = PaymentUnit.PAYMENT_INFO_CACHE_FLUSH_QUEUE, containerFactory = MQUnit.CONTAINER_FACTORY_NAME)
    public void consumePaymentInfoUpdateQueue(MapMessage mapMessage) {
        this.flushPaymentInfo(mapMessage);
    }

    public void flushPaymentInfo(MapMessage mapMessage) {
        try {
            String string = mapMessage.getString(PaymentUnit.PAYMENT_INFO_CACHE_FLUSH_MESSAGE_KEY);
            PaymentInfo paymentInfo = JSON.parseObject(string, PaymentInfo.class);
            try (Jedis jedis = jedisUtil.getJedis()) {
                if (jedis != null) {
                    String key = PaymentUnit.PAYMENT_INFO_PREFIX + paymentInfo.getOrderSn() + PaymentUnit.PAYMENT_INFO_SUFFIX;
                    jedis.setex(key, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS, string);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    public void checkPaymentResult(MapMessage mapMessage) {
        try {
            int count = mapMessage.getInt(PaymentUnit.DELAY_PAYMENT_CHECK_COUNT_KEY);
            String timeIntervalControlStr = mapMessage.getString(PaymentUnit.DELAY_PAYMENT_CHECK_TIME_CONTROL);
            long timeIntervalControl = 0;
            if (timeIntervalControlStr == null) {
                timeIntervalControl = 60000;
            } else {
                timeIntervalControl = Long.parseLong(timeIntervalControlStr) << 1;
            }
            String outTradeCode = mapMessage.getString(TradeMQUnit.OUT_TRADE_CODE);
            paymentService.checkPaymentResult(outTradeCode, count, timeIntervalControl);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
