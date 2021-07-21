package cn.com.gmall.cart.controller;

import cn.com.gmall.beans.OmsCartItem;
import cn.com.gmall.beans.PmsSkuInfo;
import cn.com.gmall.beans.PmsSkuSaleAttrValue;
import cn.com.gmall.constant.LoginCartUnit;
import cn.com.gmall.service.cart.CartService;
import cn.com.gmall.service.manage.BaseSaleAttrService;
import cn.com.gmall.service.manage.SkuService;
import cn.com.gmall.util.CookieUtil;
import cn.com.gmall.util.LoginUtil;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class CartController {
    @Reference
    private SkuService skuService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;
    @Reference
    private BaseSaleAttrService saleAttrService;
    @Autowired
    private LoginUtil loginUtil;

/*    @RequestMapping("/toTrade")
    public String toTrade(){
        return "trade";
    }*/

    /**
     * 添加购物车，如果没有登录使用uuid作为cookie添加购物车，本项目最慢的就是添加购物车
     *
     * @param skuInfo
     * @param quantity
     * @return
     */
    @RequestMapping("/addToCart")
    public String addToCart(PmsSkuInfo skuInfo, Long quantity) {
        Long id = skuInfo.getId();
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfo(id);
        if (pmsSkuInfo != null) {
            Long memberId = loginUtil.getMemberId();
            String memberNickName = loginUtil.getNickName();
            // 添加购物车，分为登录和未登录
            String cookieValue = CookieUtil.getCookieValue(request, LoginCartUnit.CART_ANONYMOUS, true);
            List<OmsCartItem> anonymousCart = cartService.listAnonymousCart(cookieValue);
            // 登录状态
            if (memberId != null) {
                if (anonymousCart == null || anonymousCart.size() == 0) {
                    // 生成cart
                    OmsCartItem omsCartItem = this.addParamsToCart(pmsSkuInfo, quantity);
                    omsCartItem.setMemberId(memberId);
                    omsCartItem.setMemberNickname(memberNickName);
                    // 这里不应该单独查询一个商品，一个个查注定会有很多损失，将所有用户商品拖入缓存，每次我们都只需要添加商品和更新缓存
                    ArrayList<OmsCartItem> memberCartItemList = cartService.listAllCartByMemberId(memberId);
                    if (memberCartItemList != null && memberCartItemList.size() > 0) {
                        // 查询出购物车里是否有该sku，需要添加缓存，如果用户反复添加，可以从缓存获取并直接修改数据库quantity，
                        boolean isDirectAdd = true;
                        for (OmsCartItem cartItem : memberCartItemList) {
                            // 如果相等则更改数量 和 totalPrice
                            if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                                cartItem.setTotalPrice(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                                if (cartItem.getId() != null) {
                                    cartService.updateCart(cartItem);
                                }
                                isDirectAdd = false;
                            }
                        }
                        if (isDirectAdd) {
                            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(BigDecimal.valueOf(omsCartItem.getQuantity())));
                            memberCartItemList.add(cartService.saveCart(omsCartItem));
                        }
                        cartService.flushCartAll(memberId, memberCartItemList);
                    } else {
                        List<OmsCartItem> cartItemList = Collections.singletonList(cartService.saveCart(omsCartItem));
                        cartService.flushCartAll(memberId, cartItemList);
                    }
                    CookieUtil.deleteCookie(request, response, LoginCartUnit.CART_ANONYMOUS);
                } else {
                    // cookie有值,并且用户添加了购物车，合并操作
                    OmsCartItem omsCartItem = this.addParamsToCart(pmsSkuInfo, quantity);
                    omsCartItem.setMemberId(memberId);
                    omsCartItem.setMemberNickname(memberNickName);
                    ArrayList<OmsCartItem> cartItemList = this.mergeCart(memberId, memberNickName, anonymousCart, omsCartItem);
                    this.flushCartAndDeleteCookie(memberId, cartItemList);
                }
                // 重定向
                return "redirect:/success.html?skuDefaultImg=" + skuInfo.getSkuDefaultImg() + "&id=" + skuInfo.getId() + "&quantity=" + quantity;

            }

            OmsCartItem omsCartItem = this.addParamsToCart(pmsSkuInfo, quantity);
            if (cookieValue == null) {
                String uuid = UUID.randomUUID().toString();
                cartService.flushAnonymous(uuid, Collections.singletonList(omsCartItem));
                // 无值则直接添加
                CookieUtil.setCookie(request, response, LoginCartUnit.CART_ANONYMOUS, uuid, LoginCartUnit.COOKIE_MAX_AGE, true);
            } else {
                // 有值则进行判断
                List<OmsCartItem> anonymousCartItems = cartService.listAnonymousCart(cookieValue);
                if (anonymousCartItems != null) {
                    boolean isDirectAdd = true;
                    for (OmsCartItem cartItem : anonymousCartItems) {
                        // 如果相等则更改数量 和 totalPrice
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cartItem.setQuantity(cartItem.getQuantity() + quantity);
                            cartItem.setTotalPrice(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                            isDirectAdd = false;
                        }
                    }
                    if (isDirectAdd) {
                        omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(BigDecimal.valueOf(omsCartItem.getQuantity())));
                        anonymousCartItems.add(omsCartItem);
                    }
                    cartService.flushAnonymous(cookieValue, anonymousCartItems);
                } else {
                    cartService.flushAnonymous(cookieValue, Collections.singletonList(omsCartItem));
                }
                CookieUtil.setCookie(request, response, LoginCartUnit.CART_ANONYMOUS, cookieValue, LoginCartUnit.COOKIE_MAX_AGE, true);
            }
        }
        // 重定向
        return "redirect:/success.html?skuDefaultImg=" + skuInfo.getSkuDefaultImg() + "&id=" + skuInfo.getId() + "&quantity=" + quantity;
    }
    //                        OmsCartItem hitCartItem = cartService.getCartExists(memberId, skuInfoId);
/*                        if (hitCartItem == null || hitCartItem.getMemberId() == null) {
                            omsCartItem.setMemberId(memberId);
                            omsCartItem.setMemberNickname(memberNickName);
                            // 直接添加
                            cartService.addCart(omsCartItem);
                            cartService.flushCartCache(skuInfoId, memberId, omsCartItem);
                        } else {
                            // 更改数量，同时也要修改缓存---------------
                            hitCartItem.setQuantity(hitCartItem.getQuantity() + quantity);

                            BigDecimal multiplier = BigDecimal.valueOf(hitCartItem.getQuantity());
                            hitCartItem.setTotalPrice(hitCartItem.getPrice().multiply(multiplier));
                            cartService.updateCartQuantity(hitCartItem, skuInfoId, memberId);
                            cartService.flushCartCache(skuInfoId, memberId, hitCartItem);
                        }*/

    @RequestMapping("/getSkuInfo")
    public String getSkuName(Long id, ModelMap modelMap, Integer quantity) {
        modelMap.put("skuInfo", skuService.getSkuInfo(id));
        modelMap.put("quantity", quantity);
        return "success";
    }

    @RequestMapping("/cartList")
    public String cartList(ModelMap modelMap) {
        // 添加购物车，分为登录和未登录
        Long memberId = loginUtil.getMemberId();
        String memberNickName = loginUtil.getNickName();

        modelMap.put("memberId", loginUtil.getMemberId());
        // 登录
        return this.cartListCommon(modelMap, memberId, memberNickName, false, null, "cartList", false, null);
    }

    /**
     * 每次选中复选框都是查出缓存来进行操作购物车，为了安全，增大了服务器压力
     *
     * @param cartItem
     * @param modelMap
     * @return
     */
    @RequestMapping("/checkCart")
    public String checkCart(OmsCartItem cartItem, ModelMap modelMap) {
        // 选中购物车，分为登录和未登录
        Long memberId = loginUtil.getMemberId();
        String memberNickName = loginUtil.getNickName();
        return this.cartListCommon(modelMap, memberId, memberNickName, true, cartItem, "cartList", false, null);
    }

    @RequestMapping("/checkCartAll")
    public String checkCartAll(Integer isChecked, ModelMap modelMap) {
        // 选中购物车，分为登录和未登录
        Long memberId = loginUtil.getMemberId();
        String memberNickName = loginUtil.getNickName();
        return this.cartListCommon(modelMap, memberId, memberNickName, false, null, "cartList", true, isChecked);
    }


    private void checkedCart(OmsCartItem cartItem, List<OmsCartItem> cartList) {
        if (cartItem != null && cartList != null && cartList.size() > 0) {
            for (OmsCartItem omsCartItem : cartList) {
                if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                    omsCartItem.setIsChecked(cartItem.getIsChecked());
                }
            }
        }
    }

    private BigDecimal totalPrice(List<OmsCartItem> cartList) {
        BigDecimal bigDecimal = new BigDecimal("0");
        if (cartList != null) {
            for (OmsCartItem omsCartItem : cartList) {
                if (omsCartItem.getIsChecked() != null && omsCartItem.getIsChecked().equals(1)) {
                    bigDecimal = bigDecimal.add(omsCartItem.getTotalPrice());
                }
            }
        }
        return bigDecimal;
    }

    /**
     * 需要将购物车合并，返回带cartId 的完整用户购物车列表，并将匿名购物车加入用户的购物车
     *
     * @param memberId
     * @param memberNickName
     * @param anonymousCartItemList
     * @param omsCartItem
     * @return
     */
    public ArrayList<OmsCartItem> mergeCart(Long memberId, String memberNickName, List<OmsCartItem> anonymousCartItemList, OmsCartItem omsCartItem) {
        // 登录了，但是cookie里还有值 2020/2/19
                   /* OmsCartItem omsCartItem = this.addParamsToCart(pmsSkuInfo, quantity);
                    omsCartItem.setMemberId(memberId);
                    omsCartItem.setMemberNickname(memberNickName);*/
        // 需要与cookie的购物车进行合并
        ArrayList<OmsCartItem> memberCartItemList = cartService.listAllCartByMemberId(memberId);
        if (omsCartItem != null) {
            anonymousCartItemList.add(omsCartItem);
        }
        // memberCarts 有值
        if (memberCartItemList != null && memberCartItemList.size() > 0) {
            ListIterator<OmsCartItem> memberIterator = memberCartItemList.listIterator();
            OmsCartItem cartItem;
            OmsCartItem anonymousCartItem;
            // 循环member的购物车
            while (memberIterator.hasNext()) {
                cartItem = memberIterator.next();
                Iterator<OmsCartItem> anonymousIterator = anonymousCartItemList.iterator();
                // 循环cookie里的购物车
                while (anonymousIterator.hasNext()) {
                    anonymousCartItem = anonymousIterator.next();
                    // 相等则修改数量和总价,并移除
                    if (cartItem.getProductSkuId().equals(anonymousCartItem.getProductSkuId())) {
                        cartItem.setQuantity(cartItem.getQuantity() + anonymousCartItem.getQuantity());
                        BigDecimal cartItemTotalPrice = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                                .add(anonymousCartItem.getPrice().multiply(BigDecimal.valueOf(anonymousCartItem.getQuantity())));
                        cartItem.setTotalPrice(cartItemTotalPrice);
                        if (cartItem.getId() != null) {
                            cartService.updateCart(cartItem);
                        }
                        anonymousIterator.remove();
                    }
                }
            }
            // 不相等则设置memberId后添加
            if (anonymousCartItemList.size() > 0) {
                for (OmsCartItem cart : anonymousCartItemList) {
                    cart.setMemberId(memberId);
                    cart.setMemberNickname(memberNickName);
                    memberCartItemList.add(cartService.saveCart(cart));
                }
            }
        } else {
            ArrayList<OmsCartItem> cartList = new ArrayList<>();
            // memberCarts 无值
            for (OmsCartItem cartItem : anonymousCartItemList) {
                cartList.add(cartService.saveCart(cartItem));
            }
            return cartList;
        }
        return memberCartItemList;
    }

    public void flushCartAndDeleteCookie(Long memberId, ArrayList<OmsCartItem> memberCartItemList) {
        // 既然已经合并购物车了，那么就进行更新缓存操作
        cartService.flushCartAll(memberId, memberCartItemList);
        CookieUtil.deleteCookie(request, response, LoginCartUnit.CART_ANONYMOUS);
    }

    private OmsCartItem addParamsToCart(PmsSkuInfo pmsSkuInfo, Long quantity) {
        BigDecimal price = new BigDecimal("0");
        BigDecimal totalPrice = new BigDecimal("0");
        OmsCartItem omsCartItem = new OmsCartItem();
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValue = skuService.listSkuSaleAttrValue(pmsSkuInfo.getId());
        Date date = new Date();
        omsCartItem.setCreateDate(date);
        omsCartItem.setModifyDate(date);
        omsCartItem.setDeleteStatus(0L);
        omsCartItem.setProductSkuId(pmsSkuInfo.getId());
        omsCartItem.setProductId(pmsSkuInfo.getSpuId());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setIsChecked(1);

        omsCartItem.setPrice(price.add(pmsSkuInfo.getPrice()));

        omsCartItem.setQuantity(quantity);
        omsCartItem.setProductAttr(JSON.toJSONString(pmsSkuSaleAttrValue));

        totalPrice = totalPrice.add(omsCartItem.getPrice()).multiply(BigDecimal.valueOf(omsCartItem.getQuantity()));
        omsCartItem.setTotalPrice(totalPrice);
        return omsCartItem;
    }

    public String cartListCommon(ModelMap modelMap, Long memberId, String memberNickName, boolean isChecked, OmsCartItem cartItem, String forwardView, boolean isCheckedAll, Integer isCheckedAllValue) {
        // 添加购物车，分为登录和未登录
        String cookieValue = CookieUtil.getCookieValue(request, LoginCartUnit.CART_ANONYMOUS, true);
        // 登录
        if (memberId != null) {
            // cookie不为空则进入检查
            if (cookieValue != null) {
                List<OmsCartItem> cartItemList = cartService.listAnonymousCart(cookieValue);
                // 合并
                if (cartItemList != null && cartItemList.size() > 0) {
                    ArrayList<OmsCartItem> cartList = this.mergeCart(memberId, memberNickName, cartItemList, null);
                    if (cartList != null) {
                        if (isCheckedAll) {
                            cartList.forEach(cart -> cart.setIsChecked(isCheckedAllValue));
                        }
                        if (isChecked) {
                            this.checkedCart(cartItem, cartList);
                        }
                        BigDecimal bigDecimal = this.totalPrice(cartList);
                        modelMap.put("cartList", cartList);
                        modelMap.put("checkedTotalPrice", bigDecimal);
                    }
                    this.flushCartAndDeleteCookie(memberId, cartList);
                    return forwardView;
                }
                // cookie的值真的为空则进行下一步，直接查询
                CookieUtil.deleteCookie(request, response, LoginCartUnit.CART_ANONYMOUS);
            }
            ArrayList<OmsCartItem> cartList = cartService.listAllCartByMemberId(memberId);
            if (cartList != null && cartList.size() > 0) {
                if (isCheckedAll) {
                    cartList.forEach(cart -> cart.setIsChecked(isCheckedAllValue));
                }
                if (isChecked) {
                    this.checkedCart(cartItem, cartList);
                }
                BigDecimal bigDecimal = this.totalPrice(cartList);
                modelMap.put("cartList", cartList);
                modelMap.put("checkedTotalPrice", bigDecimal);
                cartService.flushCartAll(memberId, cartList);
            }
            // 跳转
            return forwardView;
        }
        if (cookieValue != null) {
            List<OmsCartItem> cartList = cartService.listAnonymousCart(cookieValue);
            if (cartList != null && cartList.size() > 0) {
                if (isCheckedAll) {
                    cartList.forEach(cart -> cart.setIsChecked(isCheckedAllValue));
                }
                if (isChecked) {
                    this.checkedCart(cartItem, cartList);
                }
                BigDecimal bigDecimal = this.totalPrice(cartList);
                modelMap.put("cartList", cartList);
                modelMap.put("checkedTotalPrice", bigDecimal);
            }
            cartService.flushAnonymous(cookieValue, cartList);
            return forwardView;
        } else {
            return forwardView;
        }
    }

}
