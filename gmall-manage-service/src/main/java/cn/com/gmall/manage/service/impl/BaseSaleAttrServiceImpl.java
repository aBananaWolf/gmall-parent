package cn.com.gmall.manage.service.impl;

import cn.com.gmall.beans.PmsBaseSaleAttr;
import cn.com.gmall.manage.mapper.BaseSaleAttrMapper;
import cn.com.gmall.service.manage.BaseSaleAttrService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class BaseSaleAttrServiceImpl implements BaseSaleAttrService {
    @Autowired
    private BaseSaleAttrMapper saleAttrMapper;

    @Override
    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {
        return saleAttrMapper.selectAll();
    }

}
