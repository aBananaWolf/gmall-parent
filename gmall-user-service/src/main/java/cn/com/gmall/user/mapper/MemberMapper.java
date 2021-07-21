package cn.com.gmall.user.mapper;

import cn.com.gmall.beans.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MemberMapper extends Mapper<UmsMember> {
    List<UmsMember> selectMembers();
}
