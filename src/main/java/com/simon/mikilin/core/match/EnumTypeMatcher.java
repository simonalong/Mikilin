package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;

/**
 * 枚举类型多个判断，对应{@link FieldValidCheck#enumType()}或者{@link FieldInvalidCheck#enumType()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
public class EnumTypeMatcher implements Matcher, Builder<EnumTypeMatcher, Class[]> {

    private Class enumClass;

    @Override
    public boolean match(String name, Object object) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        // todo
        return true;
    }

    @Override
    public String errMsg() {
        return null;
    }

    @Override
    public EnumTypeMatcher build(Class[] obj) {
        return this;
    }
}
