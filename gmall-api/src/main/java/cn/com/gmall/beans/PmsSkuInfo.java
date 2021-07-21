package cn.com.gmall.beans;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class PmsSkuInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long spuId;
    @Column
    private java.math.BigDecimal price;
    @Column
    private String skuName;
    @Column
    private String skuDesc;
    @Column
    private java.math.BigDecimal weight;
    @Column
    private Long tmId;
    @Column
    private Long catalog3Id;
    @Column
    private String skuDefaultImg;

    @Transient
    private List<PmsSkuAttrValue> skuAttrValueList;
    @Transient
    private List<PmsSkuSaleAttrValue> skuSaleAttrValueList;
    @Transient
    private List<PmsSkuImage> skuImageList;
    @Transient
    private List<PmsProductSaleAttr> spuSaleAttrListCheckBySku;

    public List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySku() {
        return spuSaleAttrListCheckBySku;
    }

    public void setSpuSaleAttrListCheckBySku(List<PmsProductSaleAttr> spuSaleAttrListCheckBySku) {
        this.spuSaleAttrListCheckBySku = spuSaleAttrListCheckBySku;
    }

    public List<PmsSkuImage> getSkuImageList() {
        return skuImageList;
    }

    public void setSkuImageList(List<PmsSkuImage> skuImageList) {
        this.skuImageList = skuImageList;
    }

    public List<PmsSkuAttrValue> getSkuAttrValueList() {
        return skuAttrValueList;
    }

    public void setSkuAttrValueList(List<PmsSkuAttrValue> skuAttrValueList) {
        this.skuAttrValueList = skuAttrValueList;
    }

    public List<PmsSkuSaleAttrValue> getSkuSaleAttrValueList() {
        return skuSaleAttrValueList;
    }

    public void setSkuSaleAttrValueList(List<PmsSkuSaleAttrValue> skuSaleAttrValueList) {
        this.skuSaleAttrValueList = skuSaleAttrValueList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public java.math.BigDecimal getPrice() {
        return price;
    }

    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }


    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }


    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
    }


    public java.math.BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(java.math.BigDecimal weight) {
        this.weight = weight;
    }


    public Long getTmId() {
        return tmId;
    }

    public void setTmId(Long tmId) {
        this.tmId = tmId;
    }


    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }


    public String getSkuDefaultImg() {
        return skuDefaultImg;
    }

    public void setSkuDefaultImg(String skuDefaultImg) {
        this.skuDefaultImg = skuDefaultImg;
    }


}
