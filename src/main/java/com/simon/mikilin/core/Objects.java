package com.simon.mikilin.core;

import java.lang.reflect.InvocationTargetException;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午11:56
 */
@UtilityClass
public class Objects {

    public Object cast(Class<?> cls, String data){
        if(cls.equals(String.class)) {
            // 针对data为null的情况进行转换
            if("null".equals(data)){
                return null;
            }
            return data;
        } else if (Character.class.isAssignableFrom(cls)) {
            return data.toCharArray();
        } else {
            try {
                if("null".equals(data)){
                    return null;
                }
                return cls.getMethod("valueOf", String.class).invoke(null, data);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
