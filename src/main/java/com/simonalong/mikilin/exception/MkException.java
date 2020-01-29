package com.simonalong.mikilin.exception;

/**
 * @author zhouzhenyong
 * @since 2019-08-12 22:09
 */
public class MkException extends Exception {

    private static final String PRE = "核查异常:";

    public MkException(String message) {
        super(PRE + message);
    }

    public MkException(String message, Throwable e) {
        super(PRE + message, e);
    }

    public MkException(Throwable e) {
        super(e);
    }
}
