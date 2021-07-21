package cn.com.gmall.beans;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OmsOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long memberId;
    @Column
    private Long couponId;
    @Column
    private String orderSn;
    @Column
    private java.sql.Timestamp createTime;
    @Column
    private String memberUsername;
    @Column
    private java.math.BigDecimal totalAmount;
    @Column
    private java.math.BigDecimal payAmount;
    @Column
    private java.math.BigDecimal freightAmount;
    @Column
    private java.math.BigDecimal promotionAmount;
    @Column
    private java.math.BigDecimal integrationAmount;
    @Column
    private java.math.BigDecimal couponAmount;
    @Column
    private java.math.BigDecimal discountAmount;
    @Column
    private Integer payType;
    @Column
    private Integer sourceType;
    @Column
    private Integer status;
    @Column
    private Integer orderType;
    @Column
    private String deliveryCompany;
    @Column
    private String deliverySn;
    @Column
    private Integer autoConfirmDay;
    @Column
    private Integer integration;
    @Column
    private Integer growth;
    @Column
    private String promotionInfo;
    @Column
    private Integer billType;
    @Column
    private String billHeader;
    @Column
    private String billContent;
    @Column
    private String billReceiverPhone;
    @Column
    private String billReceiverEmail;
    @Column
    private String receiverName;
    @Column
    private String receiverPhone;
    @Column
    private String receiverPostCode;
    @Column
    private String receiverProvince;
    @Column
    private String receiverCity;
    @Column
    private String receiverRegion;
    @Column
    private String receiverDetailAddress;
    @Column
    private String note;
    @Column
    private Integer confirmStatus;
    @Column
    private Integer deleteStatus;
    @Column
    private Long useIntegration;
    @Column
    private java.sql.Timestamp paymentTime;
    @Column
    private java.sql.Timestamp deliveryTime;
    @Column
    private java.sql.Timestamp receiveTime;
    @Column
    private java.sql.Timestamp commentTime;
    @Column
    private java.sql.Timestamp modifyTime;

    @Transient
    private List<OmsOrderItem> orderItemList;

    @Transient
    private String tradeCode;

    @Transient
    private Boolean inspectionIsPass;

    @Transient
    private List<OmsCartItem> inspectionCartItemList;

    @Transient
    private List<OmsCartItem> EliminateCartItemList;

    @Transient
    private ArrayList<OmsCartItem> presentCartItemList;

    @Transient
    private ArrayList<OmsCartItem> persistentCartItemList;

    @Transient
    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<OmsCartItem> getEliminateCartItemList() {
        return EliminateCartItemList;
    }

    public void setEliminateCartItemList(List<OmsCartItem> eliminateCartItemList) {
        EliminateCartItemList = eliminateCartItemList;
    }

    public ArrayList<OmsCartItem> getPresentCartItemList() {
        return presentCartItemList;
    }

    public void setPresentCartItemList(ArrayList<OmsCartItem> presentCartItemList) {
        this.presentCartItemList = presentCartItemList;
    }

    public ArrayList<OmsCartItem> getPersistentCartItemList() {
        return persistentCartItemList;
    }

    public void setPersistentCartItemList(ArrayList<OmsCartItem> persistentCartItemList) {
        this.persistentCartItemList = persistentCartItemList;
    }

    public List<OmsCartItem> getInspectionCartItemList() {
        return inspectionCartItemList;
    }

    public void setInspectionCartItemList(List<OmsCartItem> inspectionCartItemList) {
        this.inspectionCartItemList = inspectionCartItemList;
    }

    public Boolean getInspectionIsPass() {
        return inspectionIsPass;
    }

    public void setInspectionIsPass(Boolean inspectionIsPass) {
        this.inspectionIsPass = inspectionIsPass;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    public BigDecimal getPromotionAmount() {
        return promotionAmount;
    }

    public void setPromotionAmount(BigDecimal promotionAmount) {
        this.promotionAmount = promotionAmount;
    }

    public BigDecimal getIntegrationAmount() {
        return integrationAmount;
    }

    public void setIntegrationAmount(BigDecimal integrationAmount) {
        this.integrationAmount = integrationAmount;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getDeliveryCompany() {
        return deliveryCompany;
    }

    public void setDeliveryCompany(String deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
    }

    public String getDeliverySn() {
        return deliverySn;
    }

    public void setDeliverySn(String deliverySn) {
        this.deliverySn = deliverySn;
    }

    public Integer getAutoConfirmDay() {
        return autoConfirmDay;
    }

    public void setAutoConfirmDay(Integer autoConfirmDay) {
        this.autoConfirmDay = autoConfirmDay;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public Integer getGrowth() {
        return growth;
    }

    public void setGrowth(Integer growth) {
        this.growth = growth;
    }

    public String getPromotionInfo() {
        return promotionInfo;
    }

    public void setPromotionInfo(String promotionInfo) {
        this.promotionInfo = promotionInfo;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public String getBillHeader() {
        return billHeader;
    }

    public void setBillHeader(String billHeader) {
        this.billHeader = billHeader;
    }

    public String getBillContent() {
        return billContent;
    }

    public void setBillContent(String billContent) {
        this.billContent = billContent;
    }

    public String getBillReceiverPhone() {
        return billReceiverPhone;
    }

    public void setBillReceiverPhone(String billReceiverPhone) {
        this.billReceiverPhone = billReceiverPhone;
    }

    public String getBillReceiverEmail() {
        return billReceiverEmail;
    }

    public void setBillReceiverEmail(String billReceiverEmail) {
        this.billReceiverEmail = billReceiverEmail;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverPostCode() {
        return receiverPostCode;
    }

    public void setReceiverPostCode(String receiverPostCode) {
        this.receiverPostCode = receiverPostCode;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverRegion() {
        return receiverRegion;
    }

    public void setReceiverRegion(String receiverRegion) {
        this.receiverRegion = receiverRegion;
    }

    public String getReceiverDetailAddress() {
        return receiverDetailAddress;
    }

    public void setReceiverDetailAddress(String receiverDetailAddress) {
        this.receiverDetailAddress = receiverDetailAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public Long getUseIntegration() {
        return useIntegration;
    }

    public void setUseIntegration(Long useIntegration) {
        this.useIntegration = useIntegration;
    }

    public Timestamp getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Timestamp paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Timestamp getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Timestamp deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Timestamp getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Timestamp receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Timestamp getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Timestamp commentTime) {
        this.commentTime = commentTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public List<OmsOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OmsOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }


}
