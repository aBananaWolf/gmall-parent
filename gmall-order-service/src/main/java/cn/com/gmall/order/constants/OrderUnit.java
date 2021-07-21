package cn.com.gmall.order.constants;

public interface OrderUnit {
    String USER_TRADE_CODE_PREFIX = "user:";
    String USER_TRADE_CODE_SUFFIX = ":trade";
    int USER_TRADE_CODE_EXPIRE = 60 * 30;
    String USER_TRADE_CODE_EXPIRE_UNIT = "ex";
    String USER_ORDER_PREFIX = "user:";
    String USER_ORDER_SUFFIX = ":order";
}
