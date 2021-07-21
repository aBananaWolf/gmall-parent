package cn.com.gmall.manage.controller;

import cn.com.gmall.beans.PmsSkuInfo;
import cn.com.gmall.service.manage.SkuService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 前端没有统一返回值
@RestController
@CrossOrigin(value = "${cross-origin.address}")
public class SkuController {
    @Reference
    private SkuService skuService;

    @RequestMapping("/saveSkuInfo")
    public void saveSkuInfo(@RequestBody PmsSkuInfo skuInfo) {
        skuService.saveSkuInfo(skuInfo);
    }
}
