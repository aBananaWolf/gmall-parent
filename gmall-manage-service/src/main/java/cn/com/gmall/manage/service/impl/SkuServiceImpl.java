package cn.com.gmall.manage.service.impl;

import cn.com.gmall.anno.DistributesLock;
import cn.com.gmall.beans.*;
import cn.com.gmall.constants.CacheUnit;
import cn.com.gmall.manage.constens.ManageUnit;
import cn.com.gmall.manage.mapper.*;
import cn.com.gmall.service.manage.SkuService;
import cn.com.gmall.util.CacheUtil;
import cn.com.gmall.util.JedisUtil;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuInfoMapper infoMapper;
    @Autowired
    private SkuAttrValueMapper attrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper saleAttrValueMapper;
    @Autowired
    private SkuImageMapper imageMapper;
    @Autowired
    private SpuSaleAttrListCheckBySkuMapper saleAttrCheckMapper;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    @Qualifier("skuServiceImplClass")
    private Class<?> skuServiceImplClass;


    @Override
    @Transactional
    public void saveSkuInfo(PmsSkuInfo skuInfo) {
        infoMapper.insert(skuInfo);
        for (PmsSkuAttrValue pmsSkuAttrValue : skuInfo.getSkuAttrValueList()) {
            pmsSkuAttrValue.setSkuId(skuInfo.getId());
            attrValueMapper.insert(pmsSkuAttrValue);
        }
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
            pmsSkuSaleAttrValue.setSkuId(skuInfo.getId());
            saleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
        for (PmsSkuImage pmsSkuImage : skuInfo.getSkuImageList()) {
            pmsSkuImage.setSkuId(skuInfo.getId());
            imageMapper.insert(pmsSkuImage);
        }
    }

    /**
     * 本项目的强校验也在这里进行
     * @param skuId
     * @return
     */
    @Override
    @DistributesLock(cachePrefix = ManageUnit.SKU_INFO_PREFIX, cacheSuffix = ManageUnit.SKU_INFO_SUFFIX)
    public PmsSkuInfo getSkuInfo(Long skuId) {
        Example example = new Example(PmsSkuInfo.class);
        example.createCriteria().andEqualTo("id", skuId);
        return infoMapper.selectOneByExample(example);
      /*  PmsSkuInfo pmsSkuInfo = null;
        try {
            Object instance = this.skuServiceImplClass.newInstance();
            Method originalMethod = this.skuServiceImplClass.getMethod("getSkuInfoFromDB", Long.class,SkuInfoMapper.class);
            pmsSkuInfo = cacheUtil.accessCacheByObject(PmsSkuInfo.class, instance, originalMethod, ManageUnit.SKU_INFO_PREFIX, ManageUnit.SKU_INFO_SUFFIX, skuId,infoMapper);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return pmsSkuInfo;*/
    }


/*    public PmsSkuInfo getSkuInfoFromDB(Long skuId , SkuInfoMapper infoMapper) {
        Example example = new Example(PmsSkuInfo.class);
        example.createCriteria().andEqualTo("id", skuId);
        return infoMapper.selectOneByExample(example);
    }*/

    /**
     * 查询出销售属性列表并默认选中
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @DistributesLock(cachePrefix = ManageUnit.SALE_ATTR_LIST_BY_CHECKER_PREFIX, cacheSuffix = ManageUnit.SALE_ATTR_LIST_BY_CHECKER_SUFFIX)
    public List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        /*List<PmsProductSaleAttr> pmsProductSaleAttrs = null;
        try {
            Object instance = skuServiceImplClass.newInstance();
            Method originalMethod = skuServiceImplClass.getDeclaredMethod("getSpuSaleBySkuFromDB",Long.class,Long.class,SpuSaleAttrListCheckBySkuMapper.class);
            pmsProductSaleAttrs = cacheUtil.accessCacheByList(PmsProductSaleAttr.class,instance,originalMethod,
                    ManageUnit.SALE_ATTR_LIST_BY_CHECKER_PREFIX,ManageUnit.SALE_ATTR_LIST_BY_CHECKER_SUFFIX,skuId,spuId,saleAttrCheckMapper);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }*/
        return saleAttrCheckMapper.selectSpuSaleAttrListCheckBySku(skuId, spuId);
    }


    @Override
    @DistributesLock(cachePrefix = ManageUnit.LIST_SKU_SALE_ATTR_VALUE_PREFIX, cacheSuffix = ManageUnit.LIST_SKU_SALE_ATTR_VALUE_SUFFIX)
    public List<PmsSkuSaleAttrValue> listSkuSaleAttrValue(Long skuId) {
       /* List<PmsSkuSaleAttrValue> skuSaleAttrValue= null;
        try(Jedis jedis = jedisUtil.getJedis()){
            if(jedis!=null){
                String cacheKey = ManageUnit.LIST_SKU_SALE_ATTR_VALUE_PREFIX+skuId+ManageUnit.LIST_SKU_SALE_ATTR_VALUE_SUFFIX;
                String cacheValue = jedis.get(cacheKey);
                if(cacheValue!=null){
                    skuSaleAttrValue = JSON.parseArray(cacheValue, PmsSkuSaleAttrValue.class);
                }else{
                    skuSaleAttrValue = this.listSkuSaleAttrValueAccessDB(true,skuId,jedis,skuSaleAttrValue,cacheKey);
                }
            }else{
                skuSaleAttrValue = this.listSkuSaleAttrValueAccessDB(false,skuId,jedis,skuSaleAttrValue,null);
            }
        }*/
        return this.listSkuSaleAttrValueListFromDB(skuId);
    }

    @Override
    @DistributesLock(specifyCacheKey = "id", cachePrefix = ManageUnit.FIRST_lINE_IMAGE_PREFIX, cacheSuffix = ManageUnit.FIRST_lINE_IMAGE_SUFFIX)
    public PmsSkuInfo getSkuImgList(PmsSkuInfo skuInfo) {
        Example imgExample = new Example(PmsSkuImage.class);
        imgExample.createCriteria().andEqualTo("skuId", skuInfo.getId());
        List<PmsSkuImage> pmsSkuImages = imageMapper.selectByExample(imgExample);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
       /* try {
            Object instance = skuServiceImplClass.newInstance();
            Method originalMethod = skuServiceImplClass.getDeclaredMethod("getSkuImgListFromDB",PmsSkuInfo.class,SkuImageMapper.class);
            skuInfo = cacheUtil.accessCacheByObject(skuInfo.getId(),PmsSkuInfo.class,instance,originalMethod,ManageUnit.FIRST_lINE_IMAGE_PREFIX,ManageUnit.FIRST_lINE_IMAGE_SUFFIX,skuInfo,imageMapper);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return skuInfo;*/
    }
/*    public PmsSkuInfo getSkuImgListFromDB(PmsSkuInfo skuInfo, SkuImageMapper imageMapper) {
        Example imgExample = new Example(PmsSkuImage.class);
        imgExample.createCriteria().andEqualTo("skuId", skuInfo.getId());
        List<PmsSkuImage> pmsSkuImages = imageMapper.selectByExample(imgExample);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }*/

    private List<PmsSkuSaleAttrValue> listSkuSaleAttrValueAccessDB(boolean isCache, Long skuId, Jedis jedis, List<PmsSkuSaleAttrValue> skuSaleAttrValue, String cacheKey) {
        String lockKey = ManageUnit.LIST_SKU_SALE_ATTR_VALUE_PREFIX + skuId + ManageUnit.LIST_SKU_SALE_ATTR_VALUE_LOCK_SUFFIX;
        String lockValue = UUID.randomUUID().toString();
        String ok = jedis.set(lockKey, lockValue, CacheUnit.LOCK_NX_XX, CacheUnit.LOCK_EXPIRE_UNIT, CacheUnit.LOCK_EXPIRE_TIME);
        if ("OK".equals(ok)) {
            skuSaleAttrValue = this.listSkuSaleAttrValueListFromDB(skuId);
            skuSaleAttrValue = CacheUtil.distributedLockByList(skuSaleAttrValue, lockKey, lockValue, jedis, cacheKey, isCache);
        } else {
            CacheUtil.spinSleep();
            return this.listSkuSaleAttrValue(skuId);
        }
        return skuSaleAttrValue;
    }

    public List<PmsSkuSaleAttrValue> listSkuSaleAttrValueListFromDB(Long skuId) {
        Example example = new Example(PmsSkuSaleAttrValue.class);
        example.createCriteria().andEqualTo("skuId", skuId);
        return saleAttrValueMapper.selectByExample(example);
    }


    public List<PmsProductSaleAttr> getSpuSaleBySkuFromDB(Long skuId, Long spuId, SpuSaleAttrListCheckBySkuMapper saleAttrCheckMapper) {
        return saleAttrCheckMapper.selectSpuSaleAttrListCheckBySku(skuId, spuId);
    }


    @Override
    @DistributesLock(cachePrefix = ManageUnit.ITEM_PACKAGE_PREFIX, cacheSuffix = ManageUnit.ITEM_PACKAGE_SUFFIX)
    public ItemSaleHashPackage getItemSaleHashPackageBySpuId(Long spuId) {
        //        ItemSaleHashPackage itemSaleHashPackage = null;
       /* try (Jedis jedis = jedisUtil.getJedis()){
            String s = jedis.get(ManageUnit.ITEM_PACKAGE_PREFIX + spuId + ManageUnit.ITEM_PACKAGE_SUFFIX);
            if (s != null) {
                itemSaleHashPackage = JSON.parseObject(s, ItemSaleHashPackage.class);
            } else {//ManageUnit.ITEM_PACKAGE_LOCK
                RLock lock = redissonClient.getLock(ManageUnit.ITEM_PACKAGE_LOCK);
                lock.lock(3, TimeUnit.SECONDS);
                try {*/
        //                    itemSaleHashPackage = this.getSaleAttrValueByHashFromDB(spuId);
                 /*   if (itemSaleHashPackage == null) {
                        jedis.set(ManageUnit.ITEM_PACKAGE_PREFIX + spuId + ManageUnit.ITEM_PACKAGE_SUFFIX, "");
                    } else {
                        jedis.setex(ManageUnit.ITEM_PACKAGE_PREFIX + spuId + ManageUnit.ITEM_PACKAGE_SUFFIX, CacheUnit.LOCK_EXPIRE_TIME, JSON.toJSONString(itemSaleHashPackage));
                    }
                } finally {
                    lock.unlock();
                }
            }
        }*/
        //                    itemSaleHashPackage =
        return this.getSaleAttrValueByHashFromDB(spuId);
    }

    @Override
    public List<PmsSkuInfo> getAllSkuInfo() {
        List<PmsSkuInfo> pmsSkuInfos = infoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            Example example = new Example(PmsSkuSaleAttrValue.class);
            example.createCriteria().andEqualTo("skuId", pmsSkuInfo.getId());
            pmsSkuInfo.setSkuAttrValueList(attrValueMapper.selectByExample(example));
        }
        return pmsSkuInfos;
    }


    public ItemSaleHashPackage getSaleAttrValueByHashFromDB(Long spuId) {
        List<PmsSkuInfo> saleHash = infoMapper.selectSaleAttrValueByHash(spuId);
        return this.generateSkuHashAndImg(saleHash);
    }

    // HashMap<String, String> skuHashMap, LinkedHashSet<String> firstLineImg
    public ItemSaleHashPackage generateSkuHashAndImg(List<PmsSkuInfo> saleHash) {
        HashMap<String, String> map = new HashMap<>();
        LinkedHashSet<String> img = new LinkedHashSet<>();
        for (PmsSkuInfo hash : saleHash) {
            StringBuilder stringBuilder = new StringBuilder();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : hash.getSkuSaleAttrValueList()) {
                stringBuilder.append(pmsSkuSaleAttrValue.getSaleAttrValueId());
                stringBuilder.append("|");
            }
            map.put(stringBuilder.toString(), String.valueOf(hash.getId()));
            img.add(hash.getSkuDefaultImg());
        }
        ItemSaleHashPackage itemSaleHashPackage = new ItemSaleHashPackage();
        itemSaleHashPackage.setSkuHash(JSON.toJSON(map).toString());
        itemSaleHashPackage.setFirstLineImg(img);
        itemSaleHashPackage.setLevel(saleHash.get(0).getSkuSaleAttrValueList().size());
        return itemSaleHashPackage;
    }


}
