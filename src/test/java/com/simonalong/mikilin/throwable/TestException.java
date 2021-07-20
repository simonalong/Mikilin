package com.simonalong.mikilin.throwable;

/**
 * @author shizi
 * @since 2021-07-20 19:58:20
 */
public class TestException extends RuntimeException{

    public TestException(){}
    public TestException(String message) {
        super(message);
    }
}
