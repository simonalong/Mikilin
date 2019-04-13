package com.simon.mikilin.core.util;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例工厂
 *
 * @author zhouzhenyong
 * @since 2019/4/14 上午12:58
 */
public class SingleFactory {

    private static Map<String, Object> dataMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getSingle(Class<T> tClass){
        if(null == tClass){
            return null;
        }

        return (T) dataMap.computeIfAbsent(tClass.getCanonicalName(), k->{
            try {
                Constructor constructor = tClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
