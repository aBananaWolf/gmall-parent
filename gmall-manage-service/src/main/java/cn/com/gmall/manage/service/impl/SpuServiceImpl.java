package cn.com.gmall.manage.service.impl;

import cn.com.gmall.beans.*;
import cn.com.gmall.manage.mapper.ProductImageMapper;
import cn.com.gmall.manage.mapper.ProductInfoMapper;
import cn.com.gmall.manage.mapper.ProductSaleAttrMapper;
import cn.com.gmall.manage.mapper.ProductSaleAttrValueMapper;
import cn.com.gmall.service.manage.SpuService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    private ProductInfoMapper infoMapper;
    @Autowired
    private ProductImageMapper imageMapper;
    @Autowired
    private ProductSaleAttrMapper saleAttrMapper;
    @Autowired
    private ProductSaleAttrValueMapper saleAttrValueMapper;


    @Override
    public List<PmsProductInfo> getSpuList(Integer catalog3Id) {
        Example example = new Example(PmsProductInfo.class);
        example.createCriteria().andEqualTo("catalog3Id", catalog3Id);
        return infoMapper.selectByExample(example);
    }

    @Override
    @Transactional
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        infoMapper.insert(pmsProductInfo);
        // 保存图片对象
        for (PmsProductImage pmsProductImage : pmsProductInfo.getSpuImageList()) {
            pmsProductImage.setSpuId(pmsProductInfo.getId());
            imageMapper.insert(pmsProductImage);
        }
        // 保存销售属性
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductInfo.getSpuSaleAttrList()) {
            pmsProductSaleAttr.setSpuId(pmsProductInfo.getId());
            saleAttrMapper.insert(pmsProductSaleAttr);
            // 保存销售属性值
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : pmsProductSaleAttr.getSpuSaleAttrValueList()) {
                pmsProductSaleAttrValue.setSpuId(pmsProductInfo.getId());
                pmsProductSaleAttrValue.setSaleAttrId(pmsProductSaleAttr.getSaleAttrId());
                saleAttrValueMapper.insert(pmsProductSaleAttrValue);
            }
        }
    }

    @Override
    public List<PmsProductSaleAttr> getSpuSaleAttrList(Integer spuId) {
        Example example = new Example(PmsProductSaleAttr.class);
        example.createCriteria().andEqualTo("spuId", spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = saleAttrMapper.selectByExample(example);
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrs) {
            Example valueExample = new Example(PmsProductSaleAttrValue.class);
            valueExample.createCriteria()
                    .andEqualTo("saleAttrId", pmsProductSaleAttr.getSaleAttrId())
                    .andEqualTo("spuId", spuId);
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = saleAttrValueMapper.selectByExample(valueExample);
            pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return pmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductImage> getSpuImageList(Integer spuId) {
        Example example = new Example(PmsProductImage.class);
        example.createCriteria().andEqualTo("spuId", spuId);
        return imageMapper.selectByExample(example);
    }
}
