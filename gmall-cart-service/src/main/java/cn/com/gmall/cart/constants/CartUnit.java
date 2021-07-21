package cn.com.gmall.cart.constants;

public interface CartUnit {
    /**
     * 缓存 cartExists 前缀
     */
    String CART_EXISTS_PREFIX = "cartMemberId:";
    /**
     * 缓存 cartExists 后缀
     */
    String CART_EXISTS_SUFFIX = ":exists";
    /**
     * 缓存 listAllByMemberId 前缀
     */
    String LIST_MEMBER_CART_PREFIX = "listMemberId:";
    /**
     * 缓存 listAllByMemberId 后缀
     */
    String LIST_MEMBER_CART_SUFFIX = ":allCart";
    /**
     * 缓存 cartMember 前缀
     */
    String MEMBER_CART_PREFIX = "Member:";
    /**
     * 缓存 cartMember 后缀
     */
    String MEMBER_CART_SUFFIX = ":cartMember";
    /**
     * 缓存 匿名购物车 前缀
     */
    String ANONYMOUS_CART_PREFIX = "anonymousId:";
    /**
     * 缓存 匿名购物车 后缀
     */
    String ANONYMOUS_CART_SUFFIX = ":cartAnonymous";
    /**
     * 缓存 匿名购物车列表 前缀
     */
    Object ANONYMOUS_LIST_CART_PREFIX = "anonymousIdList:";
    /**
     * 缓存 匿名购物车列表 后缀
     */
    Object ANONYMOUS_LIST_CART_SUFFIX = ":cartAnonymousList";
}
