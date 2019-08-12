package com.simonalong.mikilin.match;

import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;

/**
 * 指定的类型判断，对应{@link FieldWhiteMatcher#type()}或者{@link FieldBlackMatcher#model()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:50
 */
public class TypeMatcher extends AbstractBlackWhiteMatcher implements Builder<TypeMatcher, FieldModel> {

    private FieldModel fieldModel;

    @Override
    public boolean match(Object object, String nam, Object value) {
        if (value instanceof String) {
            if (fieldModel.valid(String.class.cast(value))) {
                setBlackMsg("属性[{0}]的值[{1}]命中[FieldModel-{2}]", nam, value, fieldModel.getName());
                return true;
            } else {
                setWhiteMsg("属性[{0}]的值[{1}]命中[FieldModel-{2}]", nam, value, fieldModel.name());
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return null == fieldModel;
    }

    @Override
    public TypeMatcher build(FieldModel obj) {
        if (obj.equals(FieldModel.DEFAULT)) {
            return null;
        }
        this.fieldModel = obj;
        return this;
    }
}
