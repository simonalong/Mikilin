package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.Builder;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 枚举类型多个判断，对应{@link Matcher#enumType()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:51
 */
@SuppressWarnings("rawtypes")
public class EnumTypeMatch extends AbstractBlackWhiteMatch implements
    Builder<EnumTypeMatch, Class<? extends Enum>[]> {

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
        } else if (value instanceof Number) {
            Number target = (Number) value;
            boolean matchResult = false;
            if (enumClass.length > 0) {
                for (Class<? extends Enum> eClass : enumClass) {
                    Enum[] enums = eClass.getEnumConstants();
                    for (Enum e : enums) {
                        if (e.ordinal() == target.intValue()) {
                            matchResult = true;
                        }
                    }
                }

                if (matchResult) {
                    setBlackMsg("属性 {0} 枚举下标 {1} 命中不允许的枚举 {2} 中的下标", name, value, getEnumStr(enumClass));
                    return true;
                }else{
                    setWhiteMsg("属性 {0} 枚举下标 {1} 没有命中枚举 {2} 中的下标", name, value, getEnumStr(enumClass));
                }
            }
        } else if (null != value && value.getClass().isEnum()) {
            if (enumClass.length > 0) {
                boolean matchResult = Stream.of(enumClass).filter(Class::isEnum).anyMatch(e -> {
                    Enum[] enums = e.getEnumConstants();
                    for (Enum item : enums) {
                        if (item == value) {
                            return true;
                        }
                    }
                    return false;
                });

                if (matchResult) {
                    setBlackMsg("属性 {0} 枚举下标 {1} 命中不允许的枚举 {2} 中的下标", name, value, getEnumStr(enumClass));
                    return true;
                }else{
                    setWhiteMsg("属性 {0} 枚举下标 {1} 没有命中枚举 {2} 中的下标", name, value, getEnumStr(enumClass));
                }
            }
        } else {
            setWhiteMsg("属性 {0} 值 {1} 没有命中枚举 {2} 对应信息", name, value, getEnumStr(enumClass));
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return (null == enumClass) || enumClass.length == 0;
    }

    @Override
    public EnumTypeMatch build(Class<? extends Enum>[] obj) {
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
