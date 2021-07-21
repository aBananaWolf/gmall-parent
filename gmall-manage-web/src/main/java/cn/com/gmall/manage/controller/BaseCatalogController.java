package cn.com.gmall.manage.controller;

import cn.com.gmall.beans.PmsBaseCatalog1;
import cn.com.gmall.beans.PmsBaseCatalog2;
import cn.com.gmall.beans.PmsBaseCatalog3;
import cn.com.gmall.service.manage.CatalogService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 三级目录
 */
// 前端没有统一返回值
@RestController
@CrossOrigin(value = "${cross-origin.address}")
public class BaseCatalogController {
    @Reference
    private CatalogService catalogService;

    @PostMapping("/getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1() {
        return catalogService.getCatalog1();
    }

    @PostMapping("/getCatalog2")
    public List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id) {
        return catalogService.getCatalog2(catalog1Id);
    }

    @PostMapping("/getCatalog3")
    public List<PmsBaseCatalog3> getCatalog3(Integer catalog2Id) {
        return catalogService.getCatalog3(catalog2Id);
    }

}
