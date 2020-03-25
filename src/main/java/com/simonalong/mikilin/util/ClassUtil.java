package com.simonalong.mikilin.util;

import java.lang.reflect.*;
import java.util.*;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/12/25 下午5:17
 */
@UtilityClass
public class ClassUtil {

    /**
     * 获取一个对象类的所有属性，包括继承的
     *
     * @param cls 待获取的类
     * @return 类的所有属性
     */
    public Set<Field> allFieldsOfClass(Class<?> cls) {
        Set<Field> fieldSet = new HashSet<>();
        fieldSet.addAll(Arrays.asList(cls.getDeclaredFields()));
        fieldSet.addAll(Arrays.asList(cls.getFields()));
        return fieldSet;
    }

    /**
     * 判断一个类型是否我们需要核查的类型
     * 1.是基本类型或者基本类型的包装类型 Boolean Byte Character Short Integer Long Double Float 2.String 类型
     * 2.java.util.Date 类型
     *
     * 注意: 其中void.class.isPrimitive() 返回true，我们这里不需要这种
     *
     * @param cls 待校验的类
     * @return true=要核查的类型，false=不用核查的类型
     */
    public boolean isCheckedType(Class<?> cls) {
        boolean baseFlag = (cls.isPrimitive() && !cls.equals(void.class));
        if (baseFlag) {
            return true;
        } else {
            if (Void.class.isAssignableFrom(cls)) {
                return false;
            }

            if (cls.isEnum()) {
                return false;
            }

            if (Number.class.isAssignableFrom(cls)){
                return true;
            }

            if (String.class.isAssignableFrom(cls)) {
                return true;
            }

            if (Date.class.isAssignableFrom(cls)) {
                return true;
            }
            try {
                return ((Class<?>) cls.getField("TYPE").get(null)).isPrimitive();
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
            }
            return false;
        }
    }

    /**
     * 解析对象包括通配符部分的结构
     *
     * <p>
     *  将对象集合或者Map对象（只关心value）拆解开，获取对应的值的类 例如：{@code Map<String, AEntity>} 到 {@code Class<AEntity>}，{@code List<BEntity>} 到 {@code Class<BEntity>}
     * <p>
     *  逻辑同{@link ObjectUtil#parseObject(Object)}，但是 parseObject 会将对象削减
     *
     * @param object 待解析对象
     * @return 解析后的对象和对象的类型：key为解析后的对象值，value为key对应的类型
     */
    public Class<?> peel(Object object) {
        if (null == object) {
            return null;
        }
        if (object instanceof Collection) {
            Collection collection = (Collection) object;
            if (!collection.isEmpty()) {
                Iterator<?> iterator = collection.iterator();
                if (iterator.hasNext()) {
                    return peel(iterator.next());
                }
            }
            return null;
        } else if (object instanceof Map) {
            Map map = (Map) object;
            return peel(map.values());
        } else if (object.getClass().isArray()) {
            return peel(Array.get(object, 0));
        } else {
            if (ClassUtil.isCheckedType(object.getClass())) {
                return null;
            }
            return object.getClass();
        }
    }
}
