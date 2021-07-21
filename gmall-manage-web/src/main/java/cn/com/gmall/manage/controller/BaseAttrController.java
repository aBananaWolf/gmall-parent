package cn.com.gmall.manage.controller;

import cn.com.gmall.beans.PmsBaseAttrInfo;
import cn.com.gmall.beans.PmsBaseAttrValue;
import cn.com.gmall.service.manage.BaseAttrService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 前端没有统一返回值
@RestController
@CrossOrigin(value = "${cross-origin.address}")
public class BaseAttrController {
    @Reference
    private BaseAttrService baseAttrService;

    @GetMapping("/attrInfoList")
    public List<PmsBaseAttrInfo> attrInfoList(Integer catalog3Id) {
        return baseAttrService.getAttrInfoList(catalog3Id);
    }

    @RequestMapping("/saveAttrInfo")
    public void saveOrUpdateAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo) {
        baseAttrService.saveOrUpdateAttrInfo(pmsBaseAttrInfo);
    }

    @PostMapping("/getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(Integer attrId) {
        return baseAttrService.getAttrValueList(attrId);
    }
}
