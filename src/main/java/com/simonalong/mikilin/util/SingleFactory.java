package com.simonalong.mikilin.util;

import com.simonalong.mikilin.spring.MkSpringBeanContext;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例工厂
 *
 * @author zhouzhenyong
 * @since 2019/4/14 上午12:58
 */
public final class SingleFactory {

    private static final Map<String, Object> dataMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getSingle(Class<T> tClass) {
        if (null == tClass) {
            return null;
        }

        try {
            T result = MkSpringBeanContext.getBean(tClass);
            if (null != result) {
                return result;
            }
        } catch (Exception ignored) {}

        return (T) dataMap.computeIfAbsent(tClass.getCanonicalName(), k -> {
            try {
                Constructor<?> constructor = tClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Exception ignored) {}
            return null;
        });
    }
}
