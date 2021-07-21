package cn.com.gmall.user.service.impl;

import cn.com.gmall.anno.DistributesLock;
import cn.com.gmall.beans.UmsMember;
import cn.com.gmall.beans.UmsMemberReceiveAddress;
import cn.com.gmall.constants.CacheUnit;
import cn.com.gmall.constants.LoginMQUnit;
import cn.com.gmall.user.constants.MemberUnit;
import cn.com.gmall.user.exception.MemberDataException;
import cn.com.gmall.user.mapper.AddressMapper;
import cn.com.gmall.user.mapper.MemberMapper;
import cn.com.gmall.service.user.MemberService;
import cn.com.gmall.util.ActiveMqUtil;
import cn.com.gmall.util.JedisUtil;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private ActiveMqUtil activeMqUtil;

    @Override
    public List<UmsMember> getMembers() {
        return memberMapper.selectAll();
    }

    @Override
    public UmsMember getMember(String username, String password) {
        // 用户数据不存在热点key
        try (Jedis jedis = jedisUtil.getJedis()) {
            String encodePassword = DigestUtils.md5Hex(password);
            if (jedis != null) {
                // username
                String passwordAndIdKey = MemberUnit.MEMBER_PASSWORD_AND_ID_PREFIX + username + MemberUnit.MEMBER_PASSWORD_AND_ID_SUFFIX;
                String passwordAndIdJson = jedis.get(passwordAndIdKey);
                if (passwordAndIdJson != null) {
                    // 缓存有值
                    Map passwordAndIdMap = JSON.parseObject(passwordAndIdJson, Map.class);
                    String cachePassword = String.valueOf(passwordAndIdMap.get(username));
                    if (cachePassword.equals(encodePassword)) {
                        // 密码正确，获取id，根据id获取info
                        Object id = passwordAndIdMap.get("id");
                        String infoKey = MemberUnit.MEMBER_KEY_PREFIX + id + MemberUnit.MEMBER_KEY_SUFFIX;
                        String infoJson = jedis.get(infoKey);
                        UmsMember umsMember = JSON.parseObject(infoJson, UmsMember.class);
                        umsMember.setPassword(null);
                        return umsMember;
                    } else {
                        return null;
                    }
                }
            }
            // 缓存无值
            Example example = new Example(UmsMember.class);
            example.createCriteria().andEqualTo("username", username);
            List<UmsMember> umsMembers = memberMapper.selectByExample(example);
            UmsMember umsMember = this.inspectUser(umsMembers);
            if (jedis == null) {
                return umsMember;
            }
            // 成功和失败都缓存值，因为jwt的关系，存id
            String infoKey = MemberUnit.MEMBER_KEY_PREFIX + umsMember.getId() + MemberUnit.MEMBER_KEY_SUFFIX;
            jedis.setex(infoKey, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS, JSON.toJSONString(umsMember));

            if (encodePassword.equals(umsMember.getPassword())) {
                umsMember.setPassword(null);
                return umsMember;
            } else {
                // 失败则缓存密码和id  username
                String passwordKey = MemberUnit.MEMBER_PASSWORD_AND_ID_PREFIX + username + MemberUnit.MEMBER_PASSWORD_AND_ID_SUFFIX;
                HashMap<String, Object> map = new HashMap<>();
                map.put(username, umsMember.getPassword());
                map.put("id", umsMember.getId());
                ;
                jedis.setex(passwordKey, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS, JSON.toJSONString(map));
                return null;
            }
        }
    }

    public UmsMember inspectUser(List<UmsMember> umsMembers) {
        if (umsMembers == null || umsMembers.size() == 0) {
            return null;
        }

        if (umsMembers.size() > 1) {
            try {
                throw new MemberDataException("出现脏数据");
            } catch (MemberDataException e) {
                e.printStackTrace();
            }
        }
        UmsMember umsMember = umsMembers.get(0);
        umsMember.setPassword(null);
        return umsMember;
    }

    @Override
    public void addToken(String token, Long id) {
        try (Jedis jedis = jedisUtil.getJedis();) {
            if (jedis != null) {
                String key = MemberUnit.TOKEN_KEY_PREFIX + id + MemberUnit.TOKEN_KEY_SUFFIX;
                jedis.setex(key, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS, token);
            }
        }
    }

    @Override
    public UmsMember checkSocialMember(String uid) {
        Example example = new Example(UmsMember.class);
        example.createCriteria().andEqualTo("sourceUid", uid);
        return memberMapper.selectOneByExample(example);
    }

    @Override
    @Transactional
    public UmsMember addMember(UmsMember member) {
        memberMapper.insertSelective(member);
        return member;
    }

    @Override
    public void updateSocialMember(UmsMember member) {
        memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    @DistributesLock(cachePrefix = MemberUnit.LIST_ADDRESS_PREFIX, cacheSuffix = MemberUnit.LIST_ADDRESS_SUFFIX)
    public List<UmsMemberReceiveAddress> listAddress(Long memberId) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        return addressMapper.selectByExample(example);
    }

    @Override
    @DistributesLock(cachePrefix = MemberUnit.MEMBER_KEY_PREFIX, cacheSuffix = MemberUnit.MEMBER_KEY_SUFFIX)
    public UmsMember getMemberByLogin(Long memberId) {
        Example example = new Example(UmsMember.class);
        example.createCriteria().andEqualTo("id", memberId);
        List<UmsMember> umsMembers = memberMapper.selectByExample(example);
        return this.inspectUser(umsMembers);
    }

    @Override
    @DistributesLock(cachePrefix = MemberUnit.GET_ADDRESS_PREFIX, cacheSuffix = MemberUnit.GET_ADDRESS_SUFFIX)
    public UmsMemberReceiveAddress getAddress(Long deliveryAddress) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("id", deliveryAddress);
        return addressMapper.selectOneByExample(example);
    }

    @Override
    public void sendMergeCartMessage(Long memberId, String memberNickName, String anonymousCartItemListCacheKey) {
        //                    ArrayList<OmsCartItem> cartList = this.mergeCart(memberId, memberNickName, cartItemList, null);
        //                    this.flushCartAndDeleteCookie(memberId, cartList);
        try {
            ActiveMQMapMessage message = new ActiveMQMapMessage();
            message.setLong(LoginMQUnit.LOGIN_CART_MERGE_MEMBER_ID, memberId);
            message.setString(LoginMQUnit.LOGIN_CART_MERGE_MEMBER_NICK_NAME, memberNickName);
            message.setString(LoginMQUnit.LOGIN_CART_MERGE_ANONYMOUS_CART_LIST_CACHE_KEY, anonymousCartItemListCacheKey);
            activeMqUtil.sendQueue(LoginMQUnit.LOGIN_SUCCESS_QUEUE, message);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}
