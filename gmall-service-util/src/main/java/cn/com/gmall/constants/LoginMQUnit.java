package cn.com.gmall.constants;

public interface LoginMQUnit {
    /**
     * 登录成功，合并购物车
     */
    String LOGIN_SUCCESS_QUEUE = "LOGIN_SUCCESS_QUEUE";
    /**
     * 用户id
     */
    String LOGIN_CART_MERGE_MEMBER_ID = "loginMemberId";
    /**
     * 用户昵称
     */
    String LOGIN_CART_MERGE_MEMBER_NICK_NAME = "loginMemberNickName";
    /**
     * 匿名购物车
     */
    String LOGIN_CART_MERGE_ANONYMOUS_CART_LIST_CACHE_KEY = "loginMemberAnonymousCartListCacheKey";
}
