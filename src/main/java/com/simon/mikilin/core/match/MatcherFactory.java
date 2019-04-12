package com.simon.mikilin.core.match;

/**
 * 匹配器工厂，用于根据对应的匹配器类型和入参，创建指定的匹配器
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午9:16
 */
public class MatcherFactory {

    public static <T, K> T build(Class<? extends Builder<T, K>> tClass, K params){
        if (null == tClass){
           return null;
        }
        try {
            return tClass.newInstance().build(params);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
