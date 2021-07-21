package cn.com.gmall.exception;


public class CacheUtilJsonParseException extends RuntimeException {
    public CacheUtilJsonParseException() {
    }

    public CacheUtilJsonParseException(String message) {
        super(message);
    }

    public CacheUtilJsonParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheUtilJsonParseException(Throwable cause) {
        super(cause);
    }

    public CacheUtilJsonParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
