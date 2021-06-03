package com.simonalong.mikilin.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author zhouzhenyong
 * @since 2020/1/29 下午8:51
 */
public class MkCheckException extends MkException {

    @Setter
    @Getter
    private Map<String, Object> errMsgMap;

    public MkCheckException(String message) {
        super(message);
    }
}
