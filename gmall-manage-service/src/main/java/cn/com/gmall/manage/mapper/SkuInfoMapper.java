package cn.com.gmall.manage.mapper;

import cn.com.gmall.beans.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuInfoMapper extends Mapper<PmsSkuInfo> {
    List<PmsSkuInfo> selectSaleAttrValueByHash(@Param("spuId") Long spuId);
}
