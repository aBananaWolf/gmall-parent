package cn.com.gmall.payment.constants;

public interface PaymentUnit {
    String PAYMENT_INFO_PREFIX = "payment:";
    String PAYMENT_INFO_SUFFIX = ":info";
    String PAYMENT_INFO_CACHE_FLUSH_QUEUE = "PAYMENT_CACHE_FLUSH_QUEUE";
    String PAYMENT_INFO_CACHE_FLUSH_MESSAGE_KEY = "PAYMENT_INFO_JSON";
    /**
     * 延迟队列开始间隔,
     */
    long DELAY_PAYMENT_CHECK_START_TIME_INTERVAL = 30000;
    /**
     * 延迟队列查询多少次
     */
    String DELAY_PAYMENT_CHECK_COUNT_KEY = "DELAY_PAYMENT_CHECK_QUEUE_COUNT";
    /**
     * 每次延迟的时间都是上一次的2倍数，6次 共 63 分钟
     */
    int DELAY_PAYMENT_CHECK_COUNT_VALUE = 6;
    /**
     * 延迟队列时间间隔控制
     */
    String DELAY_PAYMENT_CHECK_TIME_CONTROL = "TIME_INTERVAL_CONTROL";

    String DELAY_PAYMENT_CHECK_QUEUE = "DELAY_PAYMENT_CHECK_QUEUE";
    String PAYMENT_STATE = "已支付";
}
