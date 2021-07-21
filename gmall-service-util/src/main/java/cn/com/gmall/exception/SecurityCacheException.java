package cn.com.gmall.exception;

public class SecurityCacheException extends RuntimeException {
    public SecurityCacheException() {
    }

    public SecurityCacheException(String message) {
        super(message);
    }

    public SecurityCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityCacheException(Throwable cause) {
        super(cause);
    }

    public SecurityCacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
