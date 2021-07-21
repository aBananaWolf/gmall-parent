package cn.com.gmall.service.cart;

import cn.com.gmall.beans.OmsCartItem;

import java.util.ArrayList;
import java.util.List;

public interface CartService {
    //    OmsCartItem getCartExists(Long memberId, Long skuId);

    OmsCartItem saveCart(OmsCartItem omsCartItem);

    //    void updateCartQuantity(OmsCartItem cart, Long skuId, Long memberId);

    ArrayList<OmsCartItem> listAllCartByMemberId(Long memberId);

    void deleteMemberCart(Long memberId);

    //    void saveCart(List<OmsCartItem> memberCartItemList);

    /**
     * flush 操作如果redis宕掉了。。那就宕掉吧
     *
     * @param memberId
     * @param memberCartItemList
     */
    void flushCartAll(Long memberId, List<OmsCartItem> memberCartItemList);

    //    void flushCartCache(Long skuInfoId, Long memberId, OmsCartItem hitCartItem);

    List<OmsCartItem> listAnonymousCart(String cookieValue);

    void flushAnonymous(String cookieValue, List<OmsCartItem> anonymousCartItems);

    /**
     * 修改
     * @param cartItemList
     */
    void updateCartList(List<OmsCartItem> cartItemList);

    void deleteCart(OmsCartItem delCartItem);

    void updateCart(OmsCartItem cartItem);

    /**
     * 消费登录消息
     * @param memberId
     * @param memberNickName
     * @param anonymousCartListCacheKey
     */
    void consumeMergeMessage(long memberId, String memberNickName, String anonymousCartListCacheKey);
}
