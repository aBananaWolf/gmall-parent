package cn.com.gmall.search.mapper;

import cn.com.gmall.beans.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

public interface SearchByBaseAttrInfoMapper {
    List<PmsBaseAttrInfo> selectByBaseAttrInfo(@Param("finalValueIds") List<Long> finalValueIds);
}
