package com.github.simonalong.mikilin.enumtype;

import lombok.Getter;

/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:45
 */
@Getter
public enum CEnum {

    C1("C1"),
    C2("C2"),
    C3("C3");

    private String name;

    CEnum(String name) {
        this.name = name;
    }
}
