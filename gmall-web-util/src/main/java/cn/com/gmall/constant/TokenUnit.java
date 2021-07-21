package cn.com.gmall.constant;

public interface TokenUnit {
    /**
     * 老token   cookie
     */
    String OLD_TOKEN = "oldToken";
    /**
     * 老token 存活时间
     */
    int OLE_TOKEN_EXPIRE = 60 * 60 * 24;
    /**
     * 新token   url
     */
    String NEW_TOKEN = "token";
}
