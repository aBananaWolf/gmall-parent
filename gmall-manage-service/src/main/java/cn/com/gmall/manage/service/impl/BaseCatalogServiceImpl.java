package cn.com.gmall.manage.service.impl;

import cn.com.gmall.beans.PmsBaseCatalog1;
import cn.com.gmall.beans.PmsBaseCatalog2;
import cn.com.gmall.beans.PmsBaseCatalog3;
import cn.com.gmall.manage.mapper.BaseCatalog1Mapper;
import cn.com.gmall.manage.mapper.BaseCatalog2Mapper;
import cn.com.gmall.manage.mapper.BaseCatalog3Mapper;
import cn.com.gmall.service.manage.CatalogService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BaseCatalogServiceImpl implements CatalogService {

    @Autowired
    private BaseCatalog1Mapper catalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper catalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper catalog3Mapper;

    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return catalog1Mapper.selectAll();
    }

    @Override
    public List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id) {
        Example example = new Example(PmsBaseCatalog2.class);
        example.createCriteria().andEqualTo("catalog1Id", catalog1Id);
        return catalog2Mapper.selectByExample(example);
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(Integer catalog2Id) {
        Example example = new Example(PmsBaseCatalog3.class);
        example.createCriteria().andEqualTo("catalog2Id", catalog2Id);
        return catalog3Mapper.selectByExample(example);

    }
}
