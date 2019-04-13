package com.simon.mikilin.core.enumtype;

import lombok.Getter;

/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:42
 */
@Getter
public enum  BEnum {

    B1("B1"),
    B2("B2"),
    B3("B3");

    private String name;

    BEnum(String name) {
        this.name = name;
    }
}
