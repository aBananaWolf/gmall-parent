package cn.com.gmall.beans;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class OmsOrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long orderId;
    @Column
    private String orderSn;
    @Column
    private Long productId;
    @Column
    private String productPic;
    @Column
    private String productName;
    @Column
    private String productBrand;
    @Column
    private String productSn;
    @Column
    private java.math.BigDecimal productPrice;
    @Column
    private Long productQuantity;
    @Column
    private Long productSkuId;
    @Column
    private String productSkuCode;
    @Column
    private Long productCategoryId;
    @Column
    private String sp1;
    @Column
    private String sp2;
    @Column
    private String sp3;
    @Column
    private String promotionName;
    @Column
    private java.math.BigDecimal promotionAmount;
    @Column
    private java.math.BigDecimal couponAmount;
    @Column
    private java.math.BigDecimal integrationAmount;
    @Column
    private java.math.BigDecimal realAmount;
    @Column
    private Long giftIntegration;
    @Column
    private Long giftGrowth;
    @Column
    private String productAttr;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }


    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }


    public String getProductSn() {
        return productSn;
    }

    public void setProductSn(String productSn) {
        this.productSn = productSn;
    }


    public java.math.BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(java.math.BigDecimal productPrice) {
        this.productPrice = productPrice;
    }


    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }


    public Long getProductSkuId() {
        return productSkuId;
    }

    public void setProductSkuId(Long productSkuId) {
        this.productSkuId = productSkuId;
    }


    public String getProductSkuCode() {
        return productSkuCode;
    }

    public void setProductSkuCode(String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }


    public Long getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Long productCategoryId) {
        this.productCategoryId = productCategoryId;
    }


    public String getSp1() {
        return sp1;
    }

    public void setSp1(String sp1) {
        this.sp1 = sp1;
    }


    public String getSp2() {
        return sp2;
    }

    public void setSp2(String sp2) {
        this.sp2 = sp2;
    }


    public String getSp3() {
        return sp3;
    }

    public void setSp3(String sp3) {
        this.sp3 = sp3;
    }


    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }


    public java.math.BigDecimal getPromotionAmount() {
        return promotionAmount;
    }

    public void setPromotionAmount(java.math.BigDecimal promotionAmount) {
        this.promotionAmount = promotionAmount;
    }


    public java.math.BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(java.math.BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }


    public java.math.BigDecimal getIntegrationAmount() {
        return integrationAmount;
    }

    public void setIntegrationAmount(java.math.BigDecimal integrationAmount) {
        this.integrationAmount = integrationAmount;
    }


    public java.math.BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(java.math.BigDecimal realAmount) {
        this.realAmount = realAmount;
    }


    public Long getGiftIntegration() {
        return giftIntegration;
    }

    public void setGiftIntegration(Long giftIntegration) {
        this.giftIntegration = giftIntegration;
    }


    public Long getGiftGrowth() {
        return giftGrowth;
    }

    public void setGiftGrowth(Long giftGrowth) {
        this.giftGrowth = giftGrowth;
    }


    public String getProductAttr() {
        return productAttr;
    }

    public void setProductAttr(String productAttr) {
        this.productAttr = productAttr;
    }

}
