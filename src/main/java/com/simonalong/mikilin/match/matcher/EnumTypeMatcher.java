package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import com.simonalong.mikilin.match.Builder;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 枚举类型多个判断，对应{@link WhiteMatcher#enumType()}或者{@link BlackMatcher#enumType()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
@SuppressWarnings("rawtypes")
public class EnumTypeMatcher extends AbstractBlackWhiteMatcher implements
    Builder<EnumTypeMatcher, Class<? extends Enum>[]> {

    private Class<? extends Enum>[] enumClass;

    @SuppressWarnings("unchecked")
    @Override
    public boolean match(Object object, String name, Object value) {
        if(value instanceof String) {
            String target = (String) value;
            if (enumClass.length > 0) {
                boolean result = Stream.of(enumClass).filter(Class::isEnum).anyMatch(e -> {
                    try {
                        Enum.valueOf(e, target);
                        return true;
                    }catch (IllegalArgumentException illegalException){
                        return false;
                    }
                });

                if (result) {
                    setBlackMsg("属性 {0} 对象 {1} 命中不允许的枚举 {2} 中的类型", name, value, getEnumStr(enumClass));
                    return true;
                }else{
                    setWhiteMsg("属性 {0} 对象 {1} 没有命中枚举 {2} 中的类型", name, value, getEnumStr(enumClass));
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
        if (Arrays.asList(obj).isEmpty()) {
            return null;
        }
        enumClass = obj;
        return this;
    }

    private String getEnumStr(Class<? extends Enum>[] enumClasses){
        return Stream.of(enumClasses).map(Class::getSimpleName).collect(Collectors.toList()).toString();
    }
}
