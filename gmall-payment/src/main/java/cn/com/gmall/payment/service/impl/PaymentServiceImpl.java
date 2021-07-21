package cn.com.gmall.payment.service.impl;

import cn.com.gmall.anno.DistributesLock;
import cn.com.gmall.beans.PaymentInfo;
import cn.com.gmall.bo.activeMQQueueMaterial;
import cn.com.gmall.constants.CacheUnit;
import cn.com.gmall.constants.MQUnit;
import cn.com.gmall.anno.ActiveMQSecurityCache;
import cn.com.gmall.constants.TradeMQUnit;
import cn.com.gmall.payment.config.alipay.AlipayConfig;
import cn.com.gmall.payment.constants.PaymentUnit;
import cn.com.gmall.payment.mapper.PaymentInfoMapper;
import cn.com.gmall.service.order.OrderService;
import cn.com.gmall.service.payment.PaymentService;
import cn.com.gmall.util.ActiveMqUtil;
import cn.com.gmall.util.JedisUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.HashMap;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private ActiveMqUtil activeMqUtil;
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AlipayConfig alipayConfig;
    @Reference
    private OrderService orderService;

    @Override
    @Transactional
    public PaymentInfo savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
        return paymentInfo;
    }

    @Override
    @ActiveMQSecurityCache(queueName = PaymentUnit.PAYMENT_INFO_CACHE_FLUSH_QUEUE, messageName = PaymentUnit.PAYMENT_INFO_CACHE_FLUSH_MESSAGE_KEY)
    public void flushPaymentInfo(PaymentInfo paymentInfo) {
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String key = PaymentUnit.PAYMENT_INFO_PREFIX + paymentInfo.getOrderSn() + PaymentUnit.PAYMENT_INFO_SUFFIX;
                jedis.setex(key, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS, JSON.toJSONString(paymentInfo));
            }
        }
    }

    @Override
    @DistributesLock(cachePrefix = PaymentUnit.PAYMENT_INFO_PREFIX, cacheSuffix = PaymentUnit.PAYMENT_INFO_SUFFIX)
    public PaymentInfo getPaymentInfo(String orderSn) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn", orderSn);
        return paymentInfoMapper.selectOneByExample(example);
    }


    /**
     * 这里的队列不会回滚
     */
    @Override
    @Transactional
    public void updatePaymentInfo(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("id", paymentInfo.getId());

        activeMQQueueMaterial queueMaterial = activeMqUtil.getQueueMaterial();
        try {
            paymentInfoMapper.updateByExample(paymentInfo, example);
            ActiveMQMapMessage message = new ActiveMQMapMessage();
            message.setString(TradeMQUnit.OUT_TRADE_CODE, paymentInfo.getOrderSn());
            activeMqUtil.commitQueue(queueMaterial, TradeMQUnit.PAYMENT_SUCCESS_QUEUE, message);
        } catch (Exception e) {
            activeMqUtil.producerRollBack(queueMaterial);
        } finally {
            activeMqUtil.close(queueMaterial);
        }
    }

    @Override
    public void sendDelayPaymentCheckQueue(String orderSn, int delayPaymentCheckCountValue, long timeIntervalControl) {
        activeMQQueueMaterial queueMaterial = activeMqUtil.getQueueMaterial();
        try {
            ActiveMQMapMessage message = new ActiveMQMapMessage();
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, timeIntervalControl);
            message.setString(TradeMQUnit.OUT_TRADE_CODE, orderSn);
            message.setInt(PaymentUnit.DELAY_PAYMENT_CHECK_COUNT_KEY, delayPaymentCheckCountValue);
            message.setString(PaymentUnit.DELAY_PAYMENT_CHECK_TIME_CONTROL, String.valueOf(timeIntervalControl));
            activeMqUtil.commitQueue(queueMaterial, PaymentUnit.DELAY_PAYMENT_CHECK_QUEUE, message);
        } catch (Exception e) {
            activeMqUtil.producerRollBack(queueMaterial);
        } finally {
            activeMqUtil.close(queueMaterial);
        }
    }

    @Override
    public void checkPaymentResult(String outTradeCode, int count, long timeIntervalControl) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        HashMap<String, Object> map = new HashMap<>();
        map.put(TradeMQUnit.OUT_TRADE_CODE, outTradeCode);
        request.setBizContent(JSON.toJSONString(map));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null && response.isSuccess()) {
            System.out.println("调用成功，调用业务，开始幂等性检查");
            PaymentInfo paymentInfo;
            if ((paymentInfo = this.getPaymentInfo(outTradeCode)) != null
                    // 若已支付则不再发送消息
                    && paymentInfo.getPaymentStatus().equals(PaymentUnit.PAYMENT_STATE)) {
                System.out.println("已支付不再发送消息");
                return;
            } else if (paymentInfo != null
                    // 若未支付则查询
                    && !paymentInfo.getPaymentStatus().equals(PaymentUnit.PAYMENT_STATE)
                    && paymentInfo.getOrderSn().equals(response.getTradeNo())
            ) {
                System.out.println("幂等性检查通过,开始查询");
                paymentInfo.setCallbackContent(response.getBody());
                paymentInfo.setAlipayTradeNo(paymentInfo.getOrderSn());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                paymentInfo.setConfirmTime(timestamp);
                paymentInfo.setPaymentStatus(PaymentUnit.PAYMENT_STATE);
                paymentInfo.setCallbackTime(timestamp);
                this.updatePaymentInfo(paymentInfo);
                // 刷新一下缓存即可，若用户的支付信息正好被回调和主动查询同时命中，是无所谓的
                this.flushPaymentInfo(paymentInfo);
                System.out.println("查询成功");
            }
        }
        System.out.println("查询可能失败，继续放入消息队列，剩余机会" + count + "次|" + "本次延迟时间：" + timeIntervalControl / 60000 + "分钟");
        count--;
        if (count > 0) {
            this.sendDelayPaymentCheckQueue(outTradeCode, count, timeIntervalControl);
        } else {
            System.out.println("没有剩余次数，共63分钟，消除订单，真实项目不会如此草率，应该是逻辑删除，这里就不干了");
        }

    }

}
