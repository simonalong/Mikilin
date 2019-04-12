package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;

/**
 * 正则表达式判断，对应{@link FieldValidCheck#condition()}或者{@link FieldInvalidCheck#condition()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
public class ConditionMatcher implements Matcher, Builder<ConditionMatcher, String> {

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
    public ConditionMatcher build(String obj) {
        return null;
    }
}
