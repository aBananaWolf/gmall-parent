package cn.com.gmall.cart.mq;

import cn.com.gmall.constants.LoginMQUnit;
import cn.com.gmall.constants.MQUnit;
import cn.com.gmall.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class ConsumeCartServiceMessage {
    @Autowired
    private CartService cartService;

    @JmsListener(destination = LoginMQUnit.LOGIN_SUCCESS_QUEUE, containerFactory = MQUnit.CONTAINER_FACTORY_NAME)
    public void consumeLoginMergeMessage(MapMessage message) {
        try {
            long memberId = message.getLong(LoginMQUnit.LOGIN_CART_MERGE_MEMBER_ID);
            String memberNickName = message.getString(LoginMQUnit.LOGIN_CART_MERGE_MEMBER_NICK_NAME);
            String anonymousCartListCacheKey = message.getString(LoginMQUnit.LOGIN_CART_MERGE_ANONYMOUS_CART_LIST_CACHE_KEY);
            cartService.consumeMergeMessage(memberId, memberNickName, anonymousCartListCacheKey);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
