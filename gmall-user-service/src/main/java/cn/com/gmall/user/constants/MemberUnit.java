package cn.com.gmall.user.constants;

public interface MemberUnit {
    /**
     * 缓存用户信息
     */
    String MEMBER_KEY_PREFIX = "memberId:";
    String MEMBER_KEY_SUFFIX = ":info";
    /**
     * 缓存密码和id
     */
    String MEMBER_PASSWORD_AND_ID_PREFIX = "memberName:";
    String MEMBER_PASSWORD_AND_ID_SUFFIX = ":password";
    /**
     * 缓存token
     */
    String TOKEN_KEY_PREFIX = "tokenMemberId:";
    String TOKEN_KEY_SUFFIX = ":code";
    /**
     * 地址
     */
    String LIST_ADDRESS_PREFIX = "member:";
    String LIST_ADDRESS_SUFFIX = ":addressList";
    /**
     * 地址
     */
    String GET_ADDRESS_PREFIX = "member:";
    String GET_ADDRESS_SUFFIX = ":addressGet";
}
