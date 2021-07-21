package cn.com.gmall.service.order;

import cn.com.gmall.beans.OmsOrder;

public interface OrderService {
    /**
     * 生成交易码
     * @param tradeCode
     * @param memberId
     * @return
     */
    String generateTradeCode(String tradeCode, Long memberId);

    /**
     * 检查交易码
     * @param tradeCode
     * @return
     */
    boolean inspectTradeCode(String tradeCode);


    /**
     * 消除购物车，添加订单
     * @param omsOrders
     */
    OmsOrder saveOrderAndEliminateCart(OmsOrder omsOrders);


    OmsOrder getOrder(Long orderId);

    void flushOrder(OmsOrder omsOrder);

    void updateOrderStateByMQ(String outTradeCode);
}
