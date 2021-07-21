package cn.com.gmall.service.manage;

import cn.com.gmall.beans.ItemSaleHashPackage;
import cn.com.gmall.beans.PmsProductSaleAttr;
import cn.com.gmall.beans.PmsSkuInfo;
import cn.com.gmall.beans.PmsSkuSaleAttrValue;

import java.util.List;

public interface SkuService {

    void saveSkuInfo(PmsSkuInfo skuInfo);

    /**
     * skuInfo，以及强校验都在这里进行
     *
     * @param skuId
     * @return
     */
    PmsSkuInfo getSkuInfo(Long skuId);

    /**
     * 封装 int level HashMap<String, String> skuHashMap, LinkedHashSet<String> firstLineImg
     *
     * @param spuId
     * @return
     */
    ItemSaleHashPackage getItemSaleHashPackageBySpuId(Long spuId);

    List<PmsSkuInfo> getAllSkuInfo();

    /**
     * 获取默认选中的属性值，及销售属性值和列表
     *
     * @param skuId
     * @param spuId
     * @return
     */
    List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    List<PmsSkuSaleAttrValue> listSkuSaleAttrValue(Long id);

    PmsSkuInfo getSkuImgList(PmsSkuInfo skuInfo);
}
