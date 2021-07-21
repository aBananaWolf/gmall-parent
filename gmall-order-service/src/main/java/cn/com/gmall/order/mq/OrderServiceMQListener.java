package cn.com.gmall.order.mq;

import cn.com.gmall.constants.MQUnit;
import cn.com.gmall.constants.TradeMQUnit;
import cn.com.gmall.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

@Component
public class OrderServiceMQListener {
    @Autowired
    private OrderService orderService;

    @JmsListener(destination = TradeMQUnit.PAYMENT_SUCCESS_QUEUE, containerFactory = MQUnit.CONTAINER_FACTORY_NAME)
    public void consumePayResult(MapMessage message) throws JMSException {
        String outTradeCode = message.getString(TradeMQUnit.OUT_TRADE_CODE);
        orderService.updateOrderStateByMQ(outTradeCode);
    }
}
