package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldType;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import java.text.MessageFormat;

/**
 * 指定的类型判断，对应{@link FieldValidCheck#type()}或者{@link FieldInvalidCheck#type()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:50
 */
public class TypeMatcher implements Matcher, Builder<TypeMatcher, FieldType> {

    private FieldType fieldType;
    private String errMsg;

    @Override
    public boolean match(String fieldName, Object object) {
        if (object instanceof String) {
            if (fieldType.valid(String.class.cast(object))) {
                return true;
            } else {
                errMsg = MessageFormat.format("属性[{0}]的值[{1}]命中[FieldType-{2}]", fieldName, object, fieldType.name());
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return null == fieldType;
    }

    @Override
    public String errMsg() {
        return errMsg;
    }

    @Override
    public TypeMatcher build(FieldType obj) {
        if (obj.equals(FieldType.DEFAULT)){
            return null;
        }
        this.fieldType = obj;
        return this;
    }
}
