package cn.com.gmall.service.payment;

import cn.com.gmall.beans.PaymentInfo;

public interface PaymentService {
    PaymentInfo savePaymentInfo(PaymentInfo paymentInfo);

    void flushPaymentInfo(PaymentInfo paymentInfo);

    PaymentInfo getPaymentInfo(String orderSn);

    void updatePaymentInfo(PaymentInfo paymentInfo);

    void sendDelayPaymentCheckQueue(String orderSn, int delayPaymentCheckCountValue, long timeIntervalControl);

    void checkPaymentResult(String outTradeCode, int count, long timeIntervalControl);
}
