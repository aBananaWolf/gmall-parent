package cn.com.gmall.manage.constens;

public interface ManageUnit {
    /**
     * 缓存skuInfo 前缀
     */
    String SKU_INFO_PREFIX = "Sku:";
    /**
     * 缓存skuInfo 后缀
     */
    String SKU_INFO_SUFFIX = ":Info";
    /**
     * 缓存ItemSaleHashPackageBySpuId前缀
     */
    String ITEM_PACKAGE_PREFIX = "ItemSpu:";
    /**
     * 缓存ItemSaleHashPackageBySpuId后缀
     */
    String ITEM_PACKAGE_SUFFIX = ":Hash";
    /**
     * 缓存ItemSaleHashPackageBySpuId锁
     */
    String ITEM_PACKAGE_LOCK = "itemPackageLock";
    /**
     * spu销售属性值及 sku默认选中 前缀
     */
    String SALE_ATTR_LIST_BY_CHECKER_PREFIX = "SaleAttrList:";
    /**
     * spu销售属性值及 sku默认选中 后缀
     */
    String SALE_ATTR_LIST_BY_CHECKER_SUFFIX = ":Check";
    /**
     * sku属性值列表 前缀
     */
    String LIST_SKU_SALE_ATTR_VALUE_PREFIX = "listSku:";
    /**
     * sku属性值列表 后缀
     */
    String LIST_SKU_SALE_ATTR_VALUE_SUFFIX = ":SaleAttrValue";
    /**
     * sku销售属性值列表的分布式锁
     */
    String LIST_SKU_SALE_ATTR_VALUE_LOCK_SUFFIX = ":SaleAttrValue:lock";
    /**
     * sku 第一行图片 前缀
     */
    String FIRST_lINE_IMAGE_PREFIX = "skuFirst:";
    /**
     * sku 第一行图片 前缀
     */
    String FIRST_lINE_IMAGE_SUFFIX = ":image";
}
