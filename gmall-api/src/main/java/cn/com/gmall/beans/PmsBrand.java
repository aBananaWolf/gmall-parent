package cn.com.gmall.beans;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class PmsBrand implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String firstLetter;
    @Column
    private Long sort;
    @Column
    private Long factoryStatus;
    @Column
    private Long showStatus;
    @Column
    private Long productCount;
    @Column
    private Long productCommentCount;
    @Column
    private String logo;
    @Column
    private String bigPic;
    @Column
    private String brandStory;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }


    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }


    public Long getFactoryStatus() {
        return factoryStatus;
    }

    public void setFactoryStatus(Long factoryStatus) {
        this.factoryStatus = factoryStatus;
    }


    public Long getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(Long showStatus) {
        this.showStatus = showStatus;
    }


    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }


    public Long getProductCommentCount() {
        return productCommentCount;
    }

    public void setProductCommentCount(Long productCommentCount) {
        this.productCommentCount = productCommentCount;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


    public String getBigPic() {
        return bigPic;
    }

    public void setBigPic(String bigPic) {
        this.bigPic = bigPic;
    }


    public String getBrandStory() {
        return brandStory;
    }

    public void setBrandStory(String brandStory) {
        this.brandStory = brandStory;
    }

}
