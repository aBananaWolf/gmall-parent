package cn.com.gmall.service.manage;

import cn.com.gmall.beans.PmsBaseSaleAttr;

import java.util.List;

public interface BaseSaleAttrService {
    /**
     * 获取基础销售属性
     *
     * @return
     */
    List<PmsBaseSaleAttr> getBaseSaleAttrList();
}
