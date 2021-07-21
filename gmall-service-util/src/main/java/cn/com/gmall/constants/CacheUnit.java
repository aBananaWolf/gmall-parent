package cn.com.gmall.constants;

public interface CacheUnit {
    /**
     * redis lua 脚本
     * "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end"
     */
    String SCRIPT = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
    /**
     * redis普通键过期时间 15分钟
     */
    int COMMON_CACHE_KEY_EXPIRE_SECONDS = 60 * 15;
    /**
     * redis lock 后缀
     */
    String LOCK_SUFFIX = ":lock";
    /**
     * redis lock 过期时间
     */
    int LOCK_EXPIRE_TIME = 30;
    /**
     * redis lock 过期单位
     */
    String LOCK_EXPIRE_UNIT = "ex";
    /**
     * redis lock 模式 分布式锁
     */
    String LOCK_NX_XX = "nx";
}
