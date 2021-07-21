package cn.com.gmall.service.manage;

import cn.com.gmall.beans.PmsBaseCatalog1;
import cn.com.gmall.beans.PmsBaseCatalog2;
import cn.com.gmall.beans.PmsBaseCatalog3;

import java.util.List;

public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();

    List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id);

    List<PmsBaseCatalog3> getCatalog3(Integer catalog2Id);
}
