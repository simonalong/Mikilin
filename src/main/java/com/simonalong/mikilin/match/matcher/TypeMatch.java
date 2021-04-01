package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.exception.MkException;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 属性实际运行时候的类型匹配，对应{@link Matcher#type()}
 *
 * @author zhouzhenyong
 * @since 2019/8/15 下午10:34
 */
public class TypeMatch extends AbstractBlackWhiteMatch {

    private final List<Class<?>> clsList = new ArrayList<>();

    public static TypeMatch build(Field field, Class<?>[] tClasses) {
        if (null == tClasses || Arrays.asList(tClasses).isEmpty()) {
            return null;
        }
        Class<?> fieldType = field.getType();
        TypeMatch matcher = new TypeMatch();

        for (Class<?> tClass : tClasses) {
            if (!fieldType.isAssignableFrom(tClass)) {
                throw new MkException("类型不匹配：Class：" + tClass + " 无法转换为 " + fieldType.getName());
            }
        }
        matcher.clsList.addAll(Arrays.asList(tClasses));
        return matcher;
    }

    public static TypeMatch build(Parameter parameter, Class<?>[] tClasses) {
        if (null == tClasses || Arrays.asList(tClasses).isEmpty()) {
            return null;
        }
        Class<?> parameterType = parameter.getType();
        TypeMatch matcher = new TypeMatch();

        for (Class<?> tClass : tClasses) {
            if (!parameterType.isAssignableFrom(tClass)) {
                throw new MkException("类型不匹配：Class：" + tClass + " 无法转换为 " + parameterType.getName());
            }
        }
        matcher.clsList.addAll(Arrays.asList(tClasses));
        return matcher;
    }

    /**
     * 匹配到任何一个就认为匹配上
     *
     * @param object 属性所在的数据对象
     * @param name 属性名
     * @param value 待匹配的属性对应的值
     * @return true：匹配上，false：没有匹配上
     */
    @Override
    public boolean match(Object object, String name, Object value) {
        if (null == value) {
            setWhiteMsg("属性 {0} 的值为空", name);
            return false;
        }
        if (clsList.stream().anyMatch(cls -> cls.isAssignableFrom(value.getClass()))) {
            setBlackMsg("属性 {0} 的值 {1} 命中禁用类型 {2} ", name, value, clsList);
            return true;
        } else {
            setWhiteMsg("属性 {0} 的值 {1} 没有命中可用类型 {2} ", name, value, clsList);
            return false;
        }
    }

    @Override
    public boolean isEmpty() {
        return clsList.isEmpty();
    }
}
