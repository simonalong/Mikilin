package com.github.simonalong.mikilin.enumtype;

import lombok.Getter;

/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:37
 */
@Getter
public enum AEnum {
    A1("a1"),
    A2("a2"),
    A3("a3");

    private String name;

    AEnum(String name) {
        this.name = name;
    }
}
