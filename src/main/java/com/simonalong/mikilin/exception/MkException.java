package com.simonalong.mikilin.exception;

/**
 * @author zhouzhenyong
 * @since 2019-08-12 22:09
 */
public class MkException extends Exception {

    private static final String LOG_PRE = "异常-";

    public MkException(String message) {
        super(LOG_PRE + message);
    }

    public MkException(String message, Throwable e) {
        super(LOG_PRE + message, e);
    }

    public MkException(Throwable e) {
        super(e);
    }
}
