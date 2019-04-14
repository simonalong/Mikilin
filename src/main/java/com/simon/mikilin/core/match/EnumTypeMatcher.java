package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 枚举类型多个判断，对应{@link FieldValidCheck#enumType()}或者{@link FieldInvalidCheck#enumType()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
public class EnumTypeMatcher extends AbstractBlackWhiteMatcher implements Builder<EnumTypeMatcher, Class<? extends Enum>[]> {

    private Class<? extends Enum>[] enumClass;

    @SuppressWarnings("unchecked")
    @Override
    public boolean match(Object object, String name, Object value) {
        if(value instanceof String) {
            String target = String.class.cast(value);
            if (enumClass.length > 0) {
                Boolean result = Stream.of(enumClass).filter(Class::isEnum).anyMatch(e -> {
                    try {
                        Enum.valueOf(e, target);
                        return true;
                    }catch (IllegalArgumentException illegalException){
                        return false;
                    }
                });

                if (result) {
                    setBlackMsg("属性[{0}]对象[{1}]命中黑名单枚举[{2}]中的类型", name, value, getEnumStr(enumClass));
                    return true;
                }else{
                    setWhiteMsg("属性[{0}]对象[{1}]没有命中白名单枚举[{2}]中的类型", name, value, getEnumStr(enumClass));
                }
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return (null == enumClass) || enumClass.length == 0;
    }

    @Override
    public EnumTypeMatcher build(Class<? extends Enum>[] obj) {
        enumClass = obj;
        return this;
    }

    private String getEnumStr(Class<? extends Enum>[] enumClasses){
        return Stream.of(enumClasses).map(Class::getSimpleName).collect(Collectors.toList()).toString();
    }
}
