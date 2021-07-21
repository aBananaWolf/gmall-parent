package cn.com.gmall.beans;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class PmsComment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long productId;
    @Column
    private String memberNickName;
    @Column
    private String productName;
    @Column
    private Long star;
    @Column
    private String memberIp;
    @Column
    private java.sql.Timestamp createTime;
    @Column
    private Long showStatus;
    @Column
    private String productAttribute;
    @Column
    private Long collectCouont;
    @Column
    private Long readCount;
    @Column
    private String content;
    @Column
    private String pics;
    @Column
    private String memberIcon;
    @Column
    private Long replayCount;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    public String getMemberNickName() {
        return memberNickName;
    }

    public void setMemberNickName(String memberNickName) {
        this.memberNickName = memberNickName;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public Long getStar() {
        return star;
    }

    public void setStar(Long star) {
        this.star = star;
    }


    public String getMemberIp() {
        return memberIp;
    }

    public void setMemberIp(String memberIp) {
        this.memberIp = memberIp;
    }


    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
    }


    public Long getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(Long showStatus) {
        this.showStatus = showStatus;
    }


    public String getProductAttribute() {
        return productAttribute;
    }

    public void setProductAttribute(String productAttribute) {
        this.productAttribute = productAttribute;
    }


    public Long getCollectCouont() {
        return collectCouont;
    }

    public void setCollectCouont(Long collectCouont) {
        this.collectCouont = collectCouont;
    }


    public Long getReadCount() {
        return readCount;
    }

    public void setReadCount(Long readCount) {
        this.readCount = readCount;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }


    public String getMemberIcon() {
        return memberIcon;
    }

    public void setMemberIcon(String memberIcon) {
        this.memberIcon = memberIcon;
    }


    public Long getReplayCount() {
        return replayCount;
    }

    public void setReplayCount(Long replayCount) {
        this.replayCount = replayCount;
    }

}
