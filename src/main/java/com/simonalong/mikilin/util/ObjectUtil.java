package com.simonalong.mikilin.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午11:56
 */
@UtilityClass
public class ObjectUtil {

    private static final String NULL_STR = "null";

    /**
     * 将对象的数据，按照cls类型进行转换
     *
     * @param cls  待转换的Class类型
     * @param data 数据
     * @return Class类型对应的对象
     */
    public Object cast(Class<?> cls, String data) {
        if (cls.equals(String.class)) {
            // 针对data为null的情况进行转换
            if (NULL_STR.equals(data)) {
                return null;
            }
            return data;
        } else if (Character.class.isAssignableFrom(cls)) {
            return data.toCharArray();
        } else {
            try {
                if (NULL_STR.equals(data)) {
                    return null;
                }
                return cls.getMethod("valueOf", String.class).invoke(null, data);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 解析对象包括通配符部分的结构
     *
     * <p>
     *     该函数会将数据削减
     *
     * @param object 待解析对象
     * @return 解析后的对象和对象的类型：key为解析后的对象值，value为key对应的类型
     */
    public Map.Entry<Object, Class<?>> parseObject(Object object) {
        if (null == object) {
            return null;
        }
        if (object instanceof Collection) {
            Collection collection = (Collection) object;
            if (!collection.isEmpty()) {
                Iterator<?> iterator = collection.iterator();
                if (iterator.hasNext()) {
                    return parseObject(iterator.next());
                }
            }
            return null;
        } else if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            return parseObject(map.values());
        } else if (object.getClass().isArray()) {
            return parseObject(Array.get(object, 0));
        } else {
            if (ClassUtil.isCheckedType(object.getClass())) {
                return null;
            }
            return new AbstractMap.SimpleEntry<>(object, object.getClass());
        }
    }
}
