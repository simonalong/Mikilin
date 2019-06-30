package com.simonalong.mikilin.enumtype;

import lombok.Getter;

/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:42
 */
@Getter
public enum  BEnum {

    B1("b1"),
    B2("b2"),
    B3("b3");

    private String name;

    BEnum(String name) {
        this.name = name;
    }
}
