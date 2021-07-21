package cn.com.gmall.beans;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class PaymentInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String orderSn;
    @Column
    private String orderId;
    @Column
    private String alipayTradeNo;
    @Column
    private java.math.BigDecimal totalAmount;
    @Column
    private String subject;
    @Column
    private String paymentStatus;
    @Column
    private java.sql.Timestamp createTime;
    @Column
    private java.sql.Timestamp confirmTime;
    @Column
    private String callbackContent;
    @Column
    private java.sql.Timestamp callbackTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public String getAlipayTradeNo() {
        return alipayTradeNo;
    }

    public void setAlipayTradeNo(String alipayTradeNo) {
        this.alipayTradeNo = alipayTradeNo;
    }


    public java.math.BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(java.math.BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
    }


    public java.sql.Timestamp getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(java.sql.Timestamp confirmTime) {
        this.confirmTime = confirmTime;
    }


    public String getCallbackContent() {
        return callbackContent;
    }

    public void setCallbackContent(String callbackContent) {
        this.callbackContent = callbackContent;
    }


    public java.sql.Timestamp getCallbackTime() {
        return callbackTime;
    }

    public void setCallbackTime(java.sql.Timestamp callbackTime) {
        this.callbackTime = callbackTime;
    }

}
