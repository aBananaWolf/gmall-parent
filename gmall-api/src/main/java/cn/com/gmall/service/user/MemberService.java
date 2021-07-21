package cn.com.gmall.service.user;

import cn.com.gmall.beans.UmsMember;
import cn.com.gmall.beans.UmsMemberReceiveAddress;

import java.util.List;

public interface MemberService {
    List<UmsMember> getMembers();

    UmsMember getMember(String username, String password);

    void addToken(String token, Long id);

    UmsMember checkSocialMember(String uid);

    UmsMember addMember(UmsMember member);

    void updateSocialMember(UmsMember member);

    List<UmsMemberReceiveAddress> listAddress(Long memberId);

    UmsMember getMemberByLogin(Long memberId);

    UmsMemberReceiveAddress getAddress(Long deliveryAddress);

    void sendMergeCartMessage(Long memberId, String memberNickName, String anonymousCartItemListCacheKey);
}
