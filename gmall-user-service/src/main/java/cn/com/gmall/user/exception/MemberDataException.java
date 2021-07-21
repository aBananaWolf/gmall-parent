package cn.com.gmall.user.exception;

public class MemberDataException extends RuntimeException {
    public MemberDataException() {
        super();
    }

    public MemberDataException(String message) {
        super(message);
    }

    public MemberDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberDataException(Throwable cause) {
        super(cause);
    }

    protected MemberDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
