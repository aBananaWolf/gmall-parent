package cn.com.gmall.service.manage;

import cn.com.gmall.beans.PmsBaseAttrInfo;
import cn.com.gmall.beans.PmsBaseAttrValue;

import java.util.List;

public interface BaseAttrService {
    List<PmsBaseAttrInfo> getAttrInfoList(Integer catalog3Id);

    /**
     * 修改或保存一个info
     *
     * @param pmsBaseAttrInfo
     */
    void saveOrUpdateAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(Integer attrId);
}
