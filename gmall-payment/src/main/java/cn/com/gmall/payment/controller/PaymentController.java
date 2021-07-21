package cn.com.gmall.payment.controller;

import cn.com.gmall.beans.OmsOrder;
import cn.com.gmall.beans.PaymentInfo;
import cn.com.gmall.constants.MQUnit;
import cn.com.gmall.payment.config.alipay.AlipayConfig;
import cn.com.gmall.payment.constants.PaymentUnit;
import cn.com.gmall.service.order.OrderService;
import cn.com.gmall.service.payment.PaymentService;
import cn.com.gmall.util.LoginUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jms.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

@Controller
public class PaymentController {
    @Autowired
    private HttpServletRequest request;
    @Reference
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private LoginUtil loginUtil;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AlipayConfig alipayConfig;

    @RequestMapping("/alipay/submit")
    @ResponseBody
    public String alipay(Long orderId) {
        OmsOrder order = orderService.getOrder(orderId);
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayConfig.getReturn_payment_url());
        alipayRequest.setNotifyUrl(alipayConfig.getNotify_payment_url());
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no", order.getOrderSn());
        map.put("product_code", alipayConfig.getProduct_code());
        BigDecimal totalAmount = order.getPayAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
        map.put("total_amount", 0.01);
        map.put("subject", "测试商城");
        map.put("body", order.getSubject());
        alipayRequest.setBizContent(JSON.toJSONString(map));
        String body = null;
        try {
            body = alipayClient.pageExecute(alipayRequest).getBody();
            System.out.println(body);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderId(String.valueOf(order.getId()));
        paymentInfo.setOrderSn(order.getOrderSn());
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setTotalAmount(totalAmount);
        paymentInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        paymentInfo.setSubject(order.getSubject());
        // 保存不能放入消息队列，因为这是重要数据，不能让用户买单
        paymentInfo = paymentService.savePaymentInfo(paymentInfo);
        // 缓存可以放入消息队列
        paymentService.flushPaymentInfo(paymentInfo);
        System.out.println("发送延迟队列");
        paymentService.sendDelayPaymentCheckQueue(order.getOrderSn(), PaymentUnit.DELAY_PAYMENT_CHECK_COUNT_VALUE, PaymentUnit.DELAY_PAYMENT_CHECK_START_TIME_INTERVAL);
        return body;
    }

    @RequestMapping("/alipay/callback/return")
    public String aliCallBackUrl() {

        String queryString = request.getQueryString();
        String appId = request.getParameter("app_id");
        String OrderSn = request.getParameter("out_trade_no");
        String tradeNo = request.getParameter("trade_no");
        //        String totalAmount = request.getParameter("total_amount");
        PaymentInfo paymentInfo = null;
        if (alipayConfig.getApp_id().equals(appId)
                && (paymentInfo = paymentService.getPaymentInfo(OrderSn)) != null
                // 若已被修改了订单数据则不继续操作
                && !paymentInfo.getPaymentStatus().equals(PaymentUnit.PAYMENT_STATE)
                && paymentInfo.getOrderSn().equals(OrderSn)
            // 这里我们使用0.01 不用讲究
            // && paymentInfo.getTotalAmount().compareTo(new BigDecimal(totalAmount)) == 0
        ) {
            //   这里没有公网可以测试，普通的支付宝回调链接信息不全
            //   && AlipaySignature.rsaCheckV1(paramsMap,alipayConfig.getAlipay_public_key(),alipayConfig.getCharset(),alipayConfig.getSign_type());
            paymentInfo.setCallbackContent(queryString);
            paymentInfo.setAlipayTradeNo(tradeNo);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            paymentInfo.setConfirmTime(timestamp);
            paymentInfo.setPaymentStatus(PaymentUnit.PAYMENT_STATE);
            paymentInfo.setCallbackTime(timestamp);
            paymentService.updatePaymentInfo(paymentInfo);
            paymentService.flushPaymentInfo(paymentInfo);
            System.out.println("回调成功");
            return "finish";
        }
        return "finish";
    }

    @RequestMapping("/wxpay/submit")
    public String wxpay() {
        return null;
    }

    @RequestMapping("/index")
    public String index(Long orderId, ModelMap modelMap) {
        String nickName = loginUtil.getNickName();
        OmsOrder order = orderService.getOrder(orderId);
        modelMap.put("nickName", nickName);
        modelMap.put("orderId", order.getId());
        modelMap.put("orderSn", order.getOrderSn());
        modelMap.put("totalAmount", order.getPayAmount());
        return "index";
    }
}
