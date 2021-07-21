package cn.com.gmall.cart.service.impl;

import cn.com.gmall.beans.OmsCartItem;
import cn.com.gmall.anno.DistributesLock;
import cn.com.gmall.cart.constants.CartUnit;
import cn.com.gmall.cart.mapper.OmsCartItemMapper;
import cn.com.gmall.constants.CacheUnit;
import cn.com.gmall.service.cart.CartService;
import cn.com.gmall.util.CacheUtil;
import cn.com.gmall.util.JedisUtil;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Service
public class CartServiceImpl implements CartService {
    /*   @Autowired
       private redisu*/
    @Autowired
    private OmsCartItemMapper cartItemMapper;
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private JedisUtil jedisUtil;

    public String generateCartKey(Object skuId, Object memberId) {
        return skuId + "|" + memberId;
    }

   /* @Override
    @DistributesLock(pairKey = true, cachePrefix = CartUnit.CART_EXISTS_PREFIX, cacheSuffix = CartUnit.CART_EXISTS_SUFFIX)
    public OmsCartItem getCartExists(Long memberId, Long skuId) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("productSkuId", skuId).andEqualTo("memberId", memberId);
        return cartItemMapper.selectOneByExample(example);
       *//* OmsCartItem omsCartItem = null;
        try {
            Object instance = cartServiceClass.newInstance();
            Method originalMethod = cartServiceClass.getDeclaredMethod("getCartExistsFromDB", Long.class, Long.class, OmsCartItemMapper.class);
            omsCartItem = cacheUtil.accessCacheByObject(this.generateCartKey(skuId, memberId), OmsCartItem.class, instance, originalMethod,
                    CartUnit.CART_EXISTS_PREFIX, CartUnit.CART_EXISTS_SUFFIX, skuId, memberId, cartItemMapper);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return omsCartItem;*//*
    }*/

    public OmsCartItem getCartExistsFromDB(Long skuId, Long memberId, OmsCartItemMapper cartItemMapper) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("productSkuId", skuId).andEqualTo("memberId", memberId);
        return cartItemMapper.selectOneByExample(example);
    }

    @Override
    @Transactional
    public OmsCartItem saveCart(OmsCartItem omsCartItem) {
        cartItemMapper.insertSelective(omsCartItem);
        return omsCartItem;
    }

/*    @Override
    public void updateCartQuantity(OmsCartItem cart, Long skuId, Long memberId) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId", memberId).andEqualTo("productSkuId", skuId);
        cartItemMapper.updateByExampleSelective(cart, example);
    }*/

    @Override
    @DistributesLock(cachePrefix = CartUnit.LIST_MEMBER_CART_PREFIX, cacheSuffix = CartUnit.LIST_MEMBER_CART_SUFFIX)
    public ArrayList<OmsCartItem> listAllCartByMemberId(Long memberId) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        return (ArrayList<OmsCartItem>) cartItemMapper.selectByExample(example);

       /* ArrayList<OmsCartItem> cartItemArrayList = null;

        try {
            if (isCache) {
                Object instance = cartServiceClass.newInstance();
                Method originalMethod = cartServiceClass.getMethod("listAllCartByMemberIdFromDB", Long.class, OmsCartItemMapper.class);
                cartItemArrayList = cacheUtil.accessCacheByList(OmsCartItem.class, instance, originalMethod,
                        CartUnit.LIST_MEMBER_CART_PREFIX, CartUnit.LIST_MEMBER_CART_SUFFIX, memberId, cartItemMapper);
            } else {
                cartItemArrayList = this.listAllCartByMemberIdFromDB(memberId, cartItemMapper);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return cartItemArrayList;*/

    }

    public ArrayList<OmsCartItem> listAllCartByMemberIdFromDB(Long memberId, OmsCartItemMapper cartItemMapper) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        return (ArrayList<OmsCartItem>) cartItemMapper.selectByExample(example);
    }

    @Override
    public void deleteMemberCart(Long memberId) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        cartItemMapper.deleteByExample(example);
    }

/*    @Override
    @Transactional
    public void saveCart(List<OmsCartItem> memberCartItemList) {
        memberCartItemList.forEach(cart -> cartItemMapper.insertSelective(cart));
    }*/

    @Override
    public void flushCartAll(Long memberId, List<OmsCartItem> memberCartItemList) {
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String cacheKey = CartUnit.LIST_MEMBER_CART_PREFIX + memberId + CartUnit.LIST_MEMBER_CART_SUFFIX;
                jedis.setex(cacheKey, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS, JSON.toJSONString(memberCartItemList));
            }
        }
    }

/*    @Override
    public void flushCartCache(Long skuInfoId, Long memberId, OmsCartItem hitCartItem) {
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String cacheKey = CartUnit.CART_EXISTS_PREFIX + this.generateCartKey(skuInfoId, memberId) + CartUnit.CART_EXISTS_SUFFIX;
                jedis.setex(cacheKey, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS, JSON.toJSONString(hitCartItem));
            }
        }
    }*/

    @Override
    public List<OmsCartItem> listAnonymousCart(String uuid) {
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String listAnonymousCartJson = jedis.get(CartUnit.ANONYMOUS_LIST_CART_PREFIX + uuid + CartUnit.ANONYMOUS_LIST_CART_SUFFIX);
                return JSON.parseArray(listAnonymousCartJson, OmsCartItem.class);
            }
        }
        return null;
    }

    @Override
    public void flushAnonymous(String uuid, List<OmsCartItem> anonymousCartItems) {
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                jedis.setex(CartUnit.ANONYMOUS_LIST_CART_PREFIX + uuid + CartUnit.ANONYMOUS_LIST_CART_SUFFIX, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS, JSON.toJSONString(anonymousCartItems));
            }
        }
    }


    @Override
    @Transactional
    public void updateCartList(List<OmsCartItem> cartItemList) {
        for (OmsCartItem omsCartItem : cartItemList) {
            Example example = new Example(OmsCartItem.class);
            example.createCriteria().andEqualTo("id", omsCartItem.getId());
            cartItemMapper.updateByExample(omsCartItem, example);

        }
    }

    @Override
    public void deleteCart(OmsCartItem delCartItem) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id", delCartItem.getId());
        cartItemMapper.deleteByExample(example);
    }

    @Override
    public void updateCart(OmsCartItem cartItem) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria()
                .andEqualTo("memberId", cartItem.getMemberId())
                .andEqualTo("productSkuId", cartItem.getProductSkuId());
        cartItemMapper.updateByExampleSelective(cartItem, example);
    }

    @Override
    public void consumeMergeMessage(long memberId, String memberNickName, String anonymousCartListCacheKey) {
        List<OmsCartItem> anonymousCartItemList = this.listAnonymousCart(anonymousCartListCacheKey);

        if (anonymousCartItemList != null && anonymousCartItemList.size() > 0) {
            List<OmsCartItem> cartItemList = this.consumeMergeCartList(memberId, memberNickName, anonymousCartItemList);
            this.flushCartAll(memberId, cartItemList);
            this.deleteAnonymousCartList(anonymousCartListCacheKey);
        }

    }

    public List<OmsCartItem> consumeMergeCartList(long memberId, String memberNickName, List<OmsCartItem> anonymousCartItemList) {
        ArrayList<OmsCartItem> memberCartItemList = this.listAllCartByMemberId(memberId);
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
                            this.updateCart(cartItem);
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
                    memberCartItemList.add(this.saveCart(cart));
                }
            }
        } else {
            ArrayList<OmsCartItem> cartList = new ArrayList<>();
            // memberCarts 无值
            for (OmsCartItem cartItem : anonymousCartItemList) {
                cartList.add(this.saveCart(cartItem));
            }
            return cartList;
        }
        return memberCartItemList;
    }

    public void deleteAnonymousCartList(String uuid) {
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                jedis.del(CartUnit.ANONYMOUS_LIST_CART_PREFIX + uuid + CartUnit.ANONYMOUS_LIST_CART_SUFFIX);
            }
        }
    }

}
