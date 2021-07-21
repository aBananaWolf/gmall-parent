package cn.com.gmall.order.service.impl;

import cn.com.gmall.anno.DistributesLock;
import cn.com.gmall.beans.OmsCartItem;
import cn.com.gmall.beans.OmsOrder;
import cn.com.gmall.beans.OmsOrderItem;
import cn.com.gmall.bo.activeMQQueueMaterial;
import cn.com.gmall.constants.CacheUnit;
import cn.com.gmall.constants.MQUnit;
import cn.com.gmall.constants.TradeMQUnit;
import cn.com.gmall.order.constants.OrderUnit;
import cn.com.gmall.order.mapper.OrderItemMapper;
import cn.com.gmall.order.mapper.OrderMapper;
import cn.com.gmall.service.cart.CartService;
import cn.com.gmall.service.order.OrderService;
import cn.com.gmall.util.ActiveMqUtil;
import cn.com.gmall.util.JedisUtil;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Reference
    private CartService cartService;
    @Autowired
    private ActiveMqUtil activeMqUtil;

    @Override
    public String generateTradeCode(String tradeCode, Long memberId) {
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String key = OrderUnit.USER_TRADE_CODE_PREFIX + memberId + ":" + tradeCode + OrderUnit.USER_TRADE_CODE_SUFFIX;
                // 用户订单是相对唯一的，和详情页不同（所有请求一模一样）
                String ok = jedis.set(key, "", CacheUnit.LOCK_NX_XX, OrderUnit.USER_TRADE_CODE_EXPIRE_UNIT, OrderUnit.USER_TRADE_CODE_EXPIRE);
                // 无论成功失败都是返回这个key
                return key;
            }
        }
        return null;
    }

    @Override
    public boolean inspectTradeCode(String tradeCode) {
        boolean state = false;
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String script = "if redis.call('get', KEYS[1]) == '' then redis.call('del',KEYS[1]) else return 0 end";
                // 使用lua脚本，避免被攻击（并发提交）
                Long result = (Long) jedis.eval(script, Collections.singletonList(tradeCode), Collections.singletonList(tradeCode));
                if (result == null) {
                    state = true;
                }
            }
        }
        return state;
    }

    @Override
    @Transactional
    public OmsOrder saveOrderAndEliminateCart(OmsOrder omsOrders) {
        orderMapper.insertSelective(omsOrders);
        for (OmsCartItem delCartItem : omsOrders.getEliminateCartItemList()) {
            Example example = new Example(OmsOrder.class);
            example.createCriteria().andEqualTo("id", delCartItem.getId());
            if (delCartItem.getId() != null) {
                cartService.deleteCart(delCartItem);
            }
        }
        for (OmsOrderItem omsOrderItem : omsOrders.getOrderItemList()) {
            omsOrderItem.setOrderId(omsOrders.getId());
            orderItemMapper.insertSelective(omsOrderItem);
        }
        return omsOrders;
    }

    @Override
    @DistributesLock(cachePrefix = OrderUnit.USER_ORDER_PREFIX, cacheSuffix = OrderUnit.USER_ORDER_SUFFIX)
    public OmsOrder getOrder(Long orderId) {
        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("id", orderId);
        return orderMapper.selectOneByExample(example);
    }

    @Override
    public void flushOrder(OmsOrder omsOrder) {
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String key = OrderUnit.USER_ORDER_PREFIX + omsOrder.getId() + OrderUnit.USER_ORDER_SUFFIX;
                jedis.setex(key, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS, JSON.toJSONString(omsOrder));
            }
        }
    }

    @Override
    @Transactional
    public void updateOrderStateByMQ(String outTradeCode) {
        activeMQQueueMaterial queueMaterial = activeMqUtil.getQueueMaterial();
        try {
            this.updateOrderState(outTradeCode);
            ActiveMQMapMessage message = new ActiveMQMapMessage();// hash结构
            activeMqUtil.commitQueue(queueMaterial, TradeMQUnit.ORDER_ALREADY_PAID, message);
        } catch (Exception e) {
            activeMqUtil.consumerRollBack(queueMaterial);
        } finally {
            activeMqUtil.close(queueMaterial);
        }
    }

    public void updateOrderState(String outTradeCode) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setStatus(1);
        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn", outTradeCode);
        orderMapper.updateByExampleSelective(omsOrder, example);
    }
}
