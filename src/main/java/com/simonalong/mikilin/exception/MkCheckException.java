package com.simonalong.mikilin.exception;

/**
 * @author zhouzhenyong
 * @since 2020/1/29 下午8:51
 */
public class MkCheckException extends MkException {

    private static final String PRE = "匹配失败:";

    public MkCheckException(String message) {
        super(PRE + message);
    }
}
