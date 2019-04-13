package com.simon.mikilin.core.enumtype;

import lombok.Getter;

/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:37
 */
@Getter
public enum AEnum {
    A1("A1"),
    A2("A2"),
    A3("A3");

    private String name;

    AEnum(String name) {
        this.name = name;
    }
}
