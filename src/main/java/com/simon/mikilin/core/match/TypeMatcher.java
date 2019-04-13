package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldType;
import com.simon.mikilin.core.annotation.FieldValidCheck;

/**
 * 指定的类型判断，对应{@link FieldValidCheck#type()}或者{@link FieldInvalidCheck#type()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:50
 */
public class TypeMatcher extends AbstractBlackWhiteMatcher implements Builder<TypeMatcher, FieldType> {

    private FieldType fieldType;

    @Override
    public boolean match(String nam, Object object) {
        if (object instanceof String) {
            if (fieldType.valid(String.class.cast(object))) {
                setBlackMsg("属性[{0}]的值[{1}]命中[FieldType-{2}]", nam, object, fieldType.getName());
                return true;
            } else {
                setWhiteMsg("属性[{0}]的值[{1}]命中[FieldType-{2}]", nam, object, fieldType.name());
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return null == fieldType;
    }

    @Override
    public TypeMatcher build(FieldType obj) {
        if (obj.equals(FieldType.DEFAULT)) {
            return null;
        }
        this.fieldType = obj;
        return this;
    }
}
