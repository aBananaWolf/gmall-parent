package cn.com.gmall.user.service.impl;

import cn.com.gmall.beans.UmsMemberReceiveAddress;
import cn.com.gmall.user.mapper.MemberReceiveAddressMapper;
import cn.com.gmall.service.user.MemberReceiveAddressService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class MemberReceiveAddressServiceImpl implements MemberReceiveAddressService {
    @Autowired
    private MemberReceiveAddressMapper addressMapper;

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressById(Long id) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", id);
        return addressMapper.selectByExample(example);
    }
}
