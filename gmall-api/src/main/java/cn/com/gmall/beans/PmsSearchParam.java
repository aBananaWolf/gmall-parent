package cn.com.gmall.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PmsSearchParam implements Serializable {
    private Long catalog3Id;
    private String keyword;
    private List<Long> valueId;
    private List<Long> sortCrumbs;

    public List<Long> getSortCrumbs() {
        return sortCrumbs;
    }

    public void setSortCrumbs(List<Long> sortCrumbs) {
        this.sortCrumbs = sortCrumbs;
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Long> getValueId() {
        return valueId;
    }

    public void setValueId(List<Long> valueId) {
        this.valueId = valueId;
    }
}
