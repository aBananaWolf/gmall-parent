package cn.com.gmall.service.manage;

import cn.com.gmall.beans.PmsProductImage;
import cn.com.gmall.beans.PmsProductInfo;
import cn.com.gmall.beans.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> getSpuList(Integer catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> getSpuSaleAttrList(Integer spuId);

    List<PmsProductImage> getSpuImageList(Integer spuId);

}
