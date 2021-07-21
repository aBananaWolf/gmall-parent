package cn.com.gmall.order.controller;

import cn.com.gmall.beans.*;
import cn.com.gmall.order.constants.OrderUnit;
import cn.com.gmall.service.cart.CartService;
import cn.com.gmall.service.manage.SkuService;
import cn.com.gmall.service.order.OrderService;
import cn.com.gmall.service.user.MemberService;
import cn.com.gmall.util.LoginUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller
public class OrderController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private OrderService orderService;
    @Reference
    private CartService cartService;
    @Reference
    private MemberService memberService;
    @Reference
    private SkuService skuService;
    @Autowired
    private LoginUtil loginUtil;

    @RequestMapping("/submitOrder")
    public String submitOrder(Long deliveryAddress, String paymentWay, String orderComment, String tradeCode) {
        if (tradeCode == null) {
            return null;
        }
        boolean state = orderService.inspectTradeCode(tradeCode);
        if (state) {
            Long memberId = loginUtil.getMemberId();
            UmsMember umsMember = memberService.getMemberByLogin(memberId);

            UmsMemberReceiveAddress address = memberService.getAddress(deliveryAddress);

            // 生成订单，消除购物车
            OmsOrder omsOrder = this.cartToOrder(umsMember, address, orderComment, OrderUnit.ORDER_STATE_SUBMIT);
            // 检查不通过后返回结算页面
            if (omsOrder == null) {
                return "redirect:/toTrade";
            }

            // 继续进行发票操作
            this.confirmOrder(omsOrder, umsMember, address, OrderUnit.ORDER_STATE_PAY);
            // 能到这一步，说明检查通过，消除购物车，添加订单
            OmsOrder omsOrderComplete = orderService.saveOrderAndEliminateCart(omsOrder);
            cartService.flushCartAll(umsMember.getId(), omsOrderComplete.getPresentCartItemList());
            orderService.flushOrder(omsOrderComplete);
            return "redirect:http://payment.gmall.com:8013/index?orderId=" + omsOrderComplete.getId();
        }
        return "redirect:http://cart.gmall.com:8009/cartList";
    }

    @RequestMapping("/toTrade")
    public String toTrade(ModelMap modelMap) {
        Long memberId = loginUtil.getMemberId();
        UmsMember umsMember = memberService.getMemberByLogin(memberId);

        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = memberService.listAddress(memberId);

        OmsOrder omsOrders = this.cartToOrder(umsMember, null, null, null);

        String tradeCode = orderService.generateTradeCode(omsOrders.getTradeCode(), memberId);
        modelMap.put("tradeCode", tradeCode);
        modelMap.put("userAddressList", umsMemberReceiveAddresses);
        modelMap.put("orderDetailList", omsOrders.getOrderItemList());
        modelMap.put("totalAmount", omsOrders.getTotalAmount());
        return "trade";
    }

    public void confirmOrder(OmsOrder omsOrder, UmsMember user, UmsMemberReceiveAddress address, Integer orderState) {
        if (orderState != null && orderState == OrderUnit.ORDER_STATE_PAY) {
            // 发票
            omsOrder.setBillContent(null);
            omsOrder.setBillHeader(null);
            //            omsOrder.setBillReceiverEmail(user.gete);
            omsOrder.setBillReceiverPhone(address.getPhoneNumber());
            // 写死电子发票
            omsOrder.setBillType(1);
            omsOrder.setBillReceiverEmail(user.getEmail());
            // 运费
            omsOrder.setFreightAmount(null);
            // 运输
            omsOrder.setDeliverySn("物流单号");
            omsOrder.setDeliveryCompany("物流公司");

            omsOrder.setCreateTime(new Timestamp(System.currentTimeMillis()));

            Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
            omsOrder.setDeliveryTime(new Timestamp(date.getTime()));
        }
    }

    public OmsOrder cartToOrder(UmsMember user, UmsMemberReceiveAddress address, String OrderComment, Integer orderState) {
        ArrayList<OmsCartItem> cartItemArrayList = cartService.listAllCartByMemberId(user.getId());
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderItemList(new ArrayList<>());
        omsOrder.setMemberUsername(user.getUsername());
        omsOrder.setMemberId(user.getId());
        omsOrder.setPayType(0);
        omsOrder.setSourceType(0);
        omsOrder.setAutoConfirmDay(30);
        // 外部订单号，需要保证与支付层对接后订单不会重复
        String orderSn = "gmall2020226" + System.currentTimeMillis() + new SimpleDateFormat("yyyyMMddHHss").format(new Date());
        omsOrder.setOrderSn(orderSn);
        // 促销，没有
        omsOrder.setPromotionInfo("没有具体活动");

        // 生成订单
        if (orderState != null && orderState == OrderUnit.ORDER_STATE_SUBMIT) {
            // 定义一个boolean值代表校验是否通行
            omsOrder.setInspectionIsPass(true);
            // 放入一个完整的cartList，然后一步步消除
            omsOrder.setPresentCartItemList(cartItemArrayList);
            // 放入一个完整的cartList，用于强校验
            omsOrder.setPersistentCartItemList(new ArrayList<>(cartItemArrayList));
            // 如果强校验不通过则更新这里的值
            omsOrder.setInspectionCartItemList(new ArrayList<>());
            // 校验通过后使用的
            omsOrder.setEliminateCartItemList(new ArrayList<>());
            // 未支付
            omsOrder.setStatus(0);
            // 未支付
            omsOrder.setPayType(0);
            // 收货人信息
            omsOrder.setReceiverName(address.getName());
            omsOrder.setReceiverPhone(address.getPhoneNumber());
            omsOrder.setReceiverPostCode(address.getPostCode());
            omsOrder.setReceiverProvince(address.getProvince());
            omsOrder.setReceiverCity(address.getCity());
            omsOrder.setReceiverRegion(address.getRegion());
            omsOrder.setReceiverDetailAddress(address.getDetailAddress());
            omsOrder.setNote(OrderComment);
            omsOrder.setDeleteStatus(0);
            omsOrder.setModifyTime(new Timestamp(System.currentTimeMillis()));
            // 这里写死了，订单类型
            omsOrder.setOrderType(0);
        }
        Iterator<OmsCartItem> iterator = cartItemArrayList.iterator();

        StringBuilder tradeCode = new StringBuilder();
        StringBuilder subject = new StringBuilder();
        BigDecimal payMount = new BigDecimal("0");
        while (iterator.hasNext()) {
            OmsCartItem omsCartItem = iterator.next();
            // 做个判读，这里涉及很多修改操作
            if (omsCartItem.getId() == null) {
                return null;
            }

            if (omsCartItem.getIsChecked() != null && omsCartItem.getIsChecked().equals(1)) {
                // 订单详情
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                // 提交订单
                if (orderState != null && orderState == OrderUnit.ORDER_STATE_SUBMIT) {
                    // 强校验 价格, 由于应用缺陷，这里不使用缓存，有机会会来修改 2020/2/28
                    PmsSkuInfo skuInfo = skuService.getSkuInfo(omsCartItem.getProductSkuId());
                    // 强校验价格 没有库存校验
                    if (skuInfo.getPrice().compareTo(omsCartItem.getPrice()) != 0) {
                        // 重新遍历，先前的遍历的值可能会被删除
                        if (omsOrder.getInspectionIsPass()) {
                            omsOrder.setPresentCartItemList(omsOrder.getPersistentCartItemList());
                            iterator = omsOrder.getPresentCartItemList().iterator();
                            omsOrder.setInspectionIsPass(false);
                            continue;
                        }
                        // 前端没有提示功能，纠正后回到结算页面  每次修改的都是omsOrder.presentCartItemList的数据
                        omsCartItem.setPrice(skuInfo.getPrice());
                        omsCartItem.setTotalPrice(skuInfo.getPrice().multiply(BigDecimal.valueOf(omsCartItem.getQuantity())));
                        omsOrder.getInspectionCartItemList().add(omsCartItem);
                    }
                    // 强校验成功后，后面的步骤不再需要进行
                    if (!omsOrder.getInspectionIsPass()) {
                        continue;
                    }
                }

                omsOrderItem.setOrderSn(orderSn);
                omsOrderItem.setProductId(omsCartItem.getProductId());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductName(omsCartItem.getProductName());
                // 仓库的编号
                omsOrderItem.setProductSn("仓库编号");
                omsOrderItem.setProductPrice(omsCartItem.getTotalPrice());
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                omsOrderItem.setProductAttr(omsCartItem.getProductAttr());
                // 促销，没有
                omsOrderItem.setPromotionName("没有促销活动");
                // 订单详细
                omsOrder.getOrderItemList().add(omsOrderItem);

                payMount = payMount.add(omsCartItem.getTotalPrice());
                tradeCode.append(omsCartItem.getProductSkuId());
                tradeCode.append("|");

                subject.append(omsCartItem.getProductName());
                subject.append(" ");
                if (orderState != null && orderState == OrderUnit.ORDER_STATE_SUBMIT) {
                    // 需要在数据库被消除的商品
                    omsOrder.getEliminateCartItemList().add(omsCartItem);
                    // 需要被刷新缓存的商品,每次消除的都是omsOrder里的数据
                    iterator.remove();
                }
            }
        }
        if (orderState != null && orderState == OrderUnit.ORDER_STATE_SUBMIT) {
            if (!omsOrder.getInspectionIsPass()) {
                // 检查不通过，刷新cartItemList,暂时没有库存系统
                cartService.updateCartList(omsOrder.getInspectionCartItemList());
                cartService.flushCartAll(user.getId(), omsOrder.getPresentCartItemList());
                return null;
            }
            omsOrder.setSubject(subject.toString());
        }

        omsOrder.setTotalAmount(payMount);
        omsOrder.setPayAmount(payMount);
        if (orderState == null) {
            omsOrder.setTradeCode(tradeCode.toString());
        }
        return omsOrder;
    }
}
