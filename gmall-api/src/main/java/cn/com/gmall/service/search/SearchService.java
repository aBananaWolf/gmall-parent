package cn.com.gmall.service.search;

import cn.com.gmall.beans.PmsBaseAttrInfo;
import cn.com.gmall.beans.PmsSearchParam;
import cn.com.gmall.beans.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> searchByParam(PmsSearchParam pmsSearchParam);

    List<PmsBaseAttrInfo> searchByBaseAttrInfo(List<Long> finalValueIds);
}
