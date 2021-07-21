package cn.com.gmall.search.constants;

public interface SearchUnit {
    /**
     * 索引库 gmall
     */
    String GMALL_INDEX = "gmall";
    /**
     * type表 PmsSkuInfo
     */
    String GMALL_TYPE = "PmsSkuInfo";
    /**
     * PmsSkuInfo skuDesc 描述
     */
    String GMALL_SKU_DESC = "skuDesc";
    /**
     * PmsSkuInfo skuName Sku 名
     */
    String GMALL_SKU_NAME = "skuName";
    /**
     * PmsSkuInfo skuName Sku 名
     */
    String GMALL_CATALOG3_ID = "catalog3Id";
    /**
     * PmsSkuInfo 销售属性列表 的子属性  valueId
     */
    String GMALL_SKU_ATTR_VALUE_LIST_SUB_ATTR_VALUE_ID = "skuAttrValueList.valueId";
    /**
     * PmsSkuInfo 销售属性列表 的子属性  valueId
     */
    String GMALL_HIGH_LIGHT_PRETAG = "<span style='color:red'>";
    /**
     * PmsSkuInfo 销售属性列表 的子属性  valueId
     */
    String GMALL_HIGH_LIGHT_POSTTAG = "</span>";
    /**
     * PmsSkuInfo 聚合的名字
     */
    String GMALL_GROUP_BY_ATTR = "gmall_groupByAttr";
    /**
     * 平台属性值前缀
     */
    String BASE_ATTR_INFO_PREFIX = "base:";
    /**
     * 平台属性值后缀
     */
    String BASE_ATTR_INFO_SUFFIX = ":AttrInfo";
}
