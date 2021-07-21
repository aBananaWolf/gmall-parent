package cn.com.gmall.manage.controller;


import cn.com.gmall.beans.PmsBaseSaleAttr;
import cn.com.gmall.service.manage.BaseSaleAttrService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 前端没有统一返回值
@RestController
@CrossOrigin(value = "${cross-origin.address}")
public class BaseSaleAttrController {
    @Reference
    private BaseSaleAttrService baseSaleAttrService;

    @PostMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrService.getBaseSaleAttrList();
    }
}
