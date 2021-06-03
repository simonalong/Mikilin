package com.simonalong.mikilin.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author zhouzhenyong
 * @since 2019-08-12 22:09
 */
public class MkException extends RuntimeException {

    private static final String PRE = "核查异常：";
    @Setter
    @Getter
    private String funStr;
    @Setter
    @Getter
    private List<Object> parameterList;

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
