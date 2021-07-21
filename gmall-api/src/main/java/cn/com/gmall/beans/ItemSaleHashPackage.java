package cn.com.gmall.beans;

import java.io.Serializable;
import java.util.LinkedHashSet;

public class ItemSaleHashPackage implements Serializable {
    // item 页 level
    private int level;
    // spu拥有的skuHash表格
    private String skuHash;
    // 销售属性第一行的图片
    private LinkedHashSet<String> firstLineImg;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getSkuHash() {
        return skuHash;
    }

    public void setSkuHash(String skuHash) {
        this.skuHash = skuHash;
    }

    public LinkedHashSet<String> getFirstLineImg() {
        return firstLineImg;
    }

    public void setFirstLineImg(LinkedHashSet<String> firstLineImg) {
        this.firstLineImg = firstLineImg;
    }
}
