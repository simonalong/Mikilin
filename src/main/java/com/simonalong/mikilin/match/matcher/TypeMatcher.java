package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 属性实际运行时候的类型匹配，对应{@link FieldWhiteMatcher#type()}或者{@link FieldBlackMatcher#type()}
 *
 * @author zhouzhenyong
 * @since 2019/8/15 下午10:34
 */
public class TypeMatcher extends AbstractBlackWhiteMatcher {

    private List<Class<?>> clsList = new ArrayList<>();

    public static TypeMatcher build(Class<?>[] tClasses) {
        TypeMatcher matcher = new TypeMatcher();
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
