package cn.com.gmall.util;

import cn.com.gmall.constants.CacheUnit;
import cn.com.gmall.exception.CacheUtilJsonParseException;
import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class CacheUtil {
    @Autowired
    private JedisUtil jedisUtil;

    /**
     * @param cacheValue 缓存的值，如果为空则创建传入clazz
     * @param lockKey    分布式锁
     * @param lockValue  锁的值
     * @param jedis      jedis
     * @param cacheKey   缓存键
     * @param clazz      clazz
     * @param isCache    这个值代表是否需要缓存，如果redis宕掉了，那么这个值应该为false,isCache为false,则cacheKey不需要传递 为null 即可
     * @param <T>        工具类
     * @return
     */
    public static <T> T distributedLockByObject(T cacheValue, String lockKey, String lockValue, Jedis jedis, String cacheKey, Class<?> clazz, boolean isCache) {
        if (isCache) {
            if (cacheValue == null) {
                // 缓存空串，避免缓存穿透
                jedis.setex(cacheKey, 180, "");

                Object o = null;
                try {
                    o = clazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                cacheValue = (T) o;
            } else {
                // 值不为空则缓存
                //                        System.out.println("==缓存成功=="+Thread.currentThread().getId());

                jedis.setex(cacheKey, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS + new Random().nextInt(2888), JSON.toJSONString(cacheValue));
            }
        }
        jedis.eval(CacheUnit.SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(lockValue));

        return cacheValue;
    }

    public static <T> T distributedLockByList(T cacheValue, String lockKey, String lockValue, Jedis jedis, String cacheKey, /*Class<?> clazz , */boolean isCache) {
        if (isCache) {
            if (cacheValue == null) {
                // 缓存空串，避免缓存穿透
                jedis.setex(cacheKey, 180, "");
                cacheValue = (T) new ArrayList<T>();
            } else {
                // 值不为空则缓存
                //                        System.out.println("==缓存成功=="+Thread.currentThread().getId());
                jedis.setex(cacheKey, CacheUnit.COMMON_CACHE_KEY_EXPIRE_SECONDS + new Random().nextInt(2888), JSON.toJSONString(cacheValue));
            }
        }
        jedis.eval(CacheUnit.SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(lockValue));

        return cacheValue;
    }

    /**
     * 自旋休眠
     */
    public static void spinSleep() {
        try {
            Thread.sleep(new Random().nextInt(2888));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public <R> ArrayList<R> accessCacheByList(Class<R> returnClassType, Object instance, Method originalMethod,
                                              String cacheKeyPrefix, String cacheKeySuffix, Object... param) {
        return this.accessCacheByList(null, returnClassType, instance, originalMethod, cacheKeyPrefix, cacheKeySuffix, false, param);
    }

    public <R> ArrayList<R> accessCacheByList(String paramKey, Class<R> returnClassType, Object instance, Method originalMethod,
                                              String cacheKeyPrefix, String cacheKeySuffix, Object... param) {
        return this.accessCacheByList(paramKey, returnClassType, instance, originalMethod, cacheKeyPrefix, cacheKeySuffix, false, param);
    }

    public <R> ArrayList<R> accessCacheByList(Object paramKey, Class<R> returnClassType, Object instance,
                                              Method originalMethod, String cacheKeyPrefix, String cacheKeySuffix, Boolean parseCacheKey, Object... param) {
        originalMethod.setAccessible(true);

        ArrayList<R> returnInstance = null;
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String cacheKey = null;
                // param[0] 作为 cacheKey, 如果指定了 paramKey 则使用paramKey 作为 缓存key 规则
                if (paramKey != null) {
                    if (parseCacheKey) {
                        cacheKey = cacheKeyPrefix + paramKey + cacheKeySuffix;
                    } else {
                        cacheKey = cacheKeyPrefix + paramKey + cacheKeySuffix;
                    }
                } else {
                    if (parseCacheKey) {
                        cacheKey = cacheKeyPrefix + JSON.toJSONString(param[0]) + cacheKeySuffix;
                    } else {
                        cacheKey = cacheKeyPrefix + param[0] + cacheKeySuffix;
                    }
                }
                String cacheValue = jedis.get(cacheKey);
                if (cacheValue != null) {
                    // 值不为空则直接返回
                    try {
                        returnInstance = (ArrayList<R>) JSON.parseArray(cacheValue, returnClassType);
                    } catch (CacheUtilJsonParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    // jedis 不为空则可缓存 *
                    try {
                        returnInstance = accessDistributedLockByList(true, jedis, returnInstance, param, cacheKey,
                                instance, originalMethod, returnClassType, cacheKeyPrefix, cacheKeySuffix, parseCacheKey, paramKey);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    // redis宕掉了 假装我们有作为锁的jedis
                    returnInstance = accessDistributedLockByList(false, jedis, returnInstance, param, null,
                            instance, originalMethod, returnClassType, cacheKeyPrefix, cacheKeySuffix, parseCacheKey, paramKey);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnInstance;
    }

    // private static List<PmsBaseAttrInfo> searchByBaseAttrInfoAccessDB2(boolean isCache, Jedis jedis, List<PmsBaseAttrInfo> baseAttrInfosFromDB, List<Long> finalValueIds, String cacheKey) {
    private <R> ArrayList<R> accessDistributedLockByList(boolean isCache, Jedis jedis, ArrayList<R> returnInstance, Object[] param, String cacheKey,
                                                         Object instance, Method originalMethod, Class<R> returnClassType, String cacheKeyPrefix, String cacheKeySuffix, Boolean parseCacheKey, Object paramKey) throws InvocationTargetException, IllegalAccessException {
        String lockKey = cacheKey + CacheUnit.LOCK_SUFFIX;
        String lockValue = UUID.randomUUID().toString();
        String ok = jedis.set(lockKey, lockValue, CacheUnit.LOCK_NX_XX, CacheUnit.LOCK_EXPIRE_UNIT, CacheUnit.LOCK_EXPIRE_TIME);
        if ("OK".equals(ok)) {
            returnInstance = (ArrayList<R>) originalMethod.invoke(instance, param);
            returnInstance = CacheUtil.distributedLockByList(returnInstance, lockKey, lockValue, jedis, cacheKey, isCache);
        } else {
            // 自旋
            CacheUtil.spinSleep();
            return (ArrayList<R>) this.accessCacheByList(paramKey, returnClassType, instance, originalMethod, cacheKeyPrefix, cacheKeySuffix, parseCacheKey, param);
        }
        return returnInstance;
    }

    // 当前方法 method，参数 t
    // public List<PmsBaseAttrInfo> searchByBaseAttrInfo(List<Long> finalValueIds) {
    public <R> R accessCacheByObject(Class<R> returnClassType, Object instance, Method originalMethod,
                                     String cacheKeyPrefix, String cacheKeySuffix, Object... param) {
        return this.accessCacheByObject(null, returnClassType, instance, originalMethod,
                cacheKeyPrefix, cacheKeySuffix, false, param);
    }

    public <R> R accessCacheByObject(Object paramKey, Class<R> returnClassType, Object instance, Method originalMethod,
                                     String cacheKeyPrefix, String cacheKeySuffix, Object... param) {
        return this.accessCacheByObject(paramKey, returnClassType, instance, originalMethod,
                cacheKeyPrefix, cacheKeySuffix, false, param);
    }

    /**
     * pram[0] 默认会作为key 存在，这是需要注意的一点
     *
     * @param returnClassType 返回的类型
     * @param instance        含有被增强方法实例
     * @param originalMethod  源方法
     * @param cacheKeyPrefix  缓存前缀
     * @param cacheKeySuffix  缓存后缀
     * @param parseCacheKey   是否解析第一个param作为key
     * @param param           可变参数，参数为源方法的参数，参数值必须满足携带目标方法所有的参数，如：infoMapper 也需要传入 , 而且参数顺序也需要符合
     * @param <R>
     * @return
     */
    public <R> R accessCacheByObject(Object paramKey, Class<R> returnClassType, Object instance, Method originalMethod,
                                     String cacheKeyPrefix, String cacheKeySuffix, Boolean parseCacheKey, Object... param) {
        originalMethod.setAccessible(true);
        R returnInstance = null;
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String cacheKey = null;
                // 默认拿第一个参数作为key，如果指定了paramKey 则解析它
                if (paramKey != null) {
                    // 考虑到特殊需求，这里有JSON 格式的key
                    if (parseCacheKey) {
                        cacheKey = cacheKeyPrefix + JSON.toJSONString(paramKey) + cacheKeySuffix;
                    } else {
                        cacheKey = cacheKeyPrefix + paramKey + cacheKeySuffix;
                    }
                } else {
                    if (parseCacheKey) {
                        cacheKey = cacheKeyPrefix + JSON.toJSONString(param[0]) + cacheKeySuffix;
                    } else {
                        cacheKey = cacheKeyPrefix + param[0] + cacheKeySuffix;
                    }
                }
                String cacheValue = jedis.get(cacheKey);
                if (cacheValue != null) {
                    // 解析json后返回
                    try {
                        returnInstance = (R) JSON.parseObject(cacheValue, returnClassType);
                    } catch (CacheUtilJsonParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    // jedis 不为空则可缓存（redis没有宕掉）
                    try {
                        // 根据originalMethod 进行分布式锁的判断,如果自旋，那么又会回到上面jedis判空的操作
                        returnInstance = accessDistributedLockByObject(true, jedis, returnInstance, param, cacheKey,
                                instance, originalMethod, returnClassType, cacheKeyPrefix, cacheKeySuffix, parseCacheKey, paramKey);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    // redis宕掉了 假装我们有作为锁的另一个jedis ， 总是访问数据库,不进行缓存，不需要cacheKey
                    returnInstance = accessDistributedLockByObject(false, jedis, returnInstance, param, null,
                            instance, originalMethod, returnClassType, cacheKeyPrefix, cacheKeySuffix, parseCacheKey, paramKey);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnInstance;
    }

    private <R> R accessDistributedLockByObject(boolean isCache, Jedis jedis, R returnInstance, Object[] param, String cacheKey,
                                                Object instance, Method originalMethod, Class<R> returnClassType, String cacheKeyPrefix,
                                                String cacheKeySuffix, Boolean parseCacheKey, Object paramKey) throws InvocationTargetException, IllegalAccessException {
        String lockKey = cacheKey + CacheUnit.LOCK_SUFFIX;
        String lockValue = UUID.randomUUID().toString();
        String ok = jedis.set(lockKey, lockValue, CacheUnit.LOCK_NX_XX, CacheUnit.LOCK_EXPIRE_UNIT, CacheUnit.LOCK_EXPIRE_TIME);
        // 自旋则带上参数返回上一级方法，不自选则进入方法，必须是带上了目标方法所有的参数
        if ("OK".equals(ok)) {
            // 调用originMethod
            returnInstance = (R) originalMethod.invoke(instance, param);
            // 根据isCache判断是否需要缓存，但是这个方法无论如何都会删除分布式锁，isCache可以作为判断普通缓存库是否宕掉的标准
            returnInstance = CacheUtil.distributedLockByObject(returnInstance, lockKey, lockValue, jedis, cacheKey, returnClassType, isCache);
        } else {
            // 自旋
            CacheUtil.spinSleep();
            return (R) this.accessCacheByObject(paramKey, returnClassType, instance, originalMethod, cacheKeyPrefix, cacheKeySuffix, parseCacheKey, param);
        }
        return returnInstance;
    }

    /**
     * 还有其他原始工具类使用 accessCacheByObject / List ，可以直接指定key, 比较麻烦，需要指定被增强的originalMethod 方法
     *
     * @param returnClass    返回的class
     * @param isReturnList   是否是list返回值
     * @param paramKey       若指定缓存key 则使用该参数作为key
     * @param instance       实例
     * @param cacheKeyPrefix 缓存前缀
     * @param cacheKeySuffix 缓存后缀
     * @param joinPoint      aop里的执行方法（若使用自制反射则会更加麻烦，需要将mapper也一并传入）
     * @param parseCacheKey  是否解析key为json进行存储,速度慢
     * @param param          参数 默认使用param[0] 作为key，可以指定key值
     * @return 返回处理过后的值
     */
    public Object accessCacheByAOP(Class<?> returnClass, boolean isReturnList, boolean pairKey, Object paramKey, Object instance, String cacheKeyPrefix, String cacheKeySuffix, ProceedingJoinPoint joinPoint, boolean parseCacheKey, Object[] param) {

        Object returnInstance = null;
        try (Jedis jedis = jedisUtil.getJedis()) {
            if (jedis != null) {
                String cacheKey = null;
                // 默认拿第一个参数作为key，如果指定了paramKey 则解析它
                if (paramKey != null) {
                    Class<?> aClass = param[0].getClass();
                    try {
                        Field field = aClass.getDeclaredField((String) paramKey);
                        field.setAccessible(true);
                        paramKey = field.get(param[0]);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    // 考虑到特殊需求，这里有JSON 格式的key
                    if (parseCacheKey) {
                        cacheKey = cacheKeyPrefix + JSON.toJSONString(paramKey) + cacheKeySuffix;
                    } else {
                        cacheKey = cacheKeyPrefix + paramKey + cacheKeySuffix;
                    }
                } else if (pairKey) {
                    cacheKey = param[0] + ":" + param[1];
                } else {
                    if (parseCacheKey) {
                        cacheKey = cacheKeyPrefix + JSON.toJSONString(param[0]) + cacheKeySuffix;
                    } else {
                        cacheKey = cacheKeyPrefix + param[0] + cacheKeySuffix;
                    }
                }
                String cacheValue = jedis.get(cacheKey);
                if (cacheValue != null) {
                    // 解析json后返回
                    if (isReturnList) {
                        try {
                            returnInstance = JSON.parseArray(cacheValue, returnClass);
                        } catch (CacheUtilJsonParseException e) {
                            e.printStackTrace();
                            return null;
                        }
                    } else {
                        try {
                            returnInstance = JSON.parseObject(cacheValue, returnClass);
                        } catch (CacheUtilJsonParseException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                } else {
                    // jedis 不为空则可缓存（redis没有宕掉）
                    try {
                        // 根据originalMethod 进行分布式锁的判断,如果自旋，那么又会回到上面jedis判空的操作
                        returnInstance = accessDistributedLockAOP(true, jedis, returnInstance, param, cacheKey,
                                instance, cacheKeyPrefix, cacheKeySuffix, parseCacheKey, paramKey, pairKey, joinPoint, isReturnList, returnClass);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            } else {
                try {
                    // redis宕掉了 假装我们有作为锁的另一个jedis ， 总是访问数据库,不进行缓存，不需要cacheKey
                    returnInstance = accessDistributedLockAOP(false, jedis, returnInstance, param, null,
                            instance, cacheKeyPrefix, cacheKeySuffix, parseCacheKey, paramKey, pairKey, joinPoint, isReturnList, returnClass);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
        return returnInstance;
    }

    private Object accessDistributedLockAOP(boolean isCache, Jedis jedis, Object returnInstance, Object[] param, String cacheKey,
                                            Object instance, String cacheKeyPrefix, String cacheKeySuffix, Boolean parseCacheKey,
                                            Object paramKey, boolean pairKey, ProceedingJoinPoint joinPoint, boolean isReturnList, Class<?> returnClass) throws Throwable {
        String lockKey = cacheKey + CacheUnit.LOCK_SUFFIX;
        String lockValue = UUID.randomUUID().toString();
        String ok = jedis.set(lockKey, lockValue, CacheUnit.LOCK_NX_XX, CacheUnit.LOCK_EXPIRE_UNIT, CacheUnit.LOCK_EXPIRE_TIME);
        // 自旋则带上参数返回上一级方法，不自旋则进入方法，必须是带上了目标方法所有的参数
        if ("OK".equals(ok)) {
            // 调用originMethod
            returnInstance = joinPoint.proceed();
            // 根据isCache判断是否需要缓存，但是这个方法无论如何都会删除分布式锁，isCache可以作为判断普通缓存库是否宕掉的标准
            // 这里可以不用区分List和Object，个人原因把这个留了下来
            if (isReturnList) {
                returnInstance = CacheUtil.distributedLockByList(returnInstance, lockKey, lockValue, jedis, cacheKey, isCache);
            } else {
                returnInstance = CacheUtil.distributedLockByObject(returnInstance, lockKey, lockValue, jedis, cacheKey, returnClass, isCache);
            }
        } else {
            // 自旋
            CacheUtil.spinSleep();
            return this.accessCacheByAOP(returnClass, isReturnList, pairKey, paramKey, instance, cacheKeyPrefix, cacheKeySuffix, joinPoint, parseCacheKey, param);
        }
        return returnInstance;
    }
}
