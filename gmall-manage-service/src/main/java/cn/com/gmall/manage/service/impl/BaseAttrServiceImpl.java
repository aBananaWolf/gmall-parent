package cn.com.gmall.manage.service.impl;

import cn.com.gmall.beans.PmsBaseAttrInfo;
import cn.com.gmall.beans.PmsBaseAttrValue;
import cn.com.gmall.manage.mapper.BaseAttrInfoMapper;
import cn.com.gmall.manage.mapper.BaseAttrValueMapper;
import cn.com.gmall.manage.mapper.BaseSaleAttrMapper;
import cn.com.gmall.service.manage.BaseAttrService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BaseAttrServiceImpl implements BaseAttrService {
    @Autowired
    private BaseAttrInfoMapper infoMapper;
    @Autowired
    private BaseAttrValueMapper valueMapper;
    @Autowired
    private BaseSaleAttrMapper saleAttrMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(Integer catalog3Id) {
        Example example = new Example(PmsBaseAttrInfo.class);
        example.createCriteria().andEqualTo("catalog3Id", catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = infoMapper.selectByExample(example);
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            Example valueExample = new Example(PmsBaseAttrValue.class);
            valueExample.createCriteria().andEqualTo("attrId", pmsBaseAttrInfo.getId());
            List<PmsBaseAttrValue> pmsBaseAttrValues = valueMapper.selectByExample(valueExample);
            pmsBaseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }
        return pmsBaseAttrInfos;
    }

    @Override
    @Transactional
    public void saveOrUpdateAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        Long id = pmsBaseAttrInfo.getId();
        if (StringUtils.isEmpty(id)) {
            this.addAttrInfo(pmsBaseAttrInfo);
        } else {
            Example infoExample = new Example(PmsBaseAttrInfo.class);
            infoExample.createCriteria().andEqualTo("id", id);
            infoMapper.deleteByExample(infoExample);

            Example valueExample = new Example(PmsBaseAttrValue.class);
            valueExample.createCriteria().andEqualTo("attrId", id);
            valueMapper.deleteByExample(valueExample);
            this.addAttrInfo(pmsBaseAttrInfo);
        }

    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(Integer attrId) {
        Example example = new Example(PmsBaseAttrValue.class);
        example.createCriteria().andEqualTo("attrId", attrId);
        return valueMapper.selectByExample(example);
    }


    private void addAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        infoMapper.insertSelective(pmsBaseAttrInfo);
        for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrInfo.getAttrValueList()) {
            pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
            valueMapper.insertSelective(pmsBaseAttrValue);
        }
    }
}
