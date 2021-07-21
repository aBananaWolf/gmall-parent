package cn.com.gmall.constants;

public interface TradeMQUnit {

    /**
     * 外部订单号
     */
    String OUT_TRADE_CODE = "out_trade_no";
    /**
     * 支付成功，修改订单状态
     */
    String PAYMENT_SUCCESS_QUEUE = "PAYMENT_SUCCESS_QUEUE";
    /**
     * 订单状态已修改，发送消息给库存
     */
    String ORDER_ALREADY_PAID = "ORDER_ALREADY_PAID_QUEUE";
}
