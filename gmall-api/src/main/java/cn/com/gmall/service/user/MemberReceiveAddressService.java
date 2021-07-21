package cn.com.gmall.service.user;

import cn.com.gmall.beans.UmsMemberReceiveAddress;

import java.util.List;

public interface MemberReceiveAddressService {
    List<UmsMemberReceiveAddress> getReceiveAddressById(Long id);
}
