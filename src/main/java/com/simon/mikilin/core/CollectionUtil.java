package com.simon.mikilin.core;

import java.util.Collection;
import java.util.Map;
import lombok.experimental.UtilityClass;

/**
 * 尽量不依赖外部的工具
 * @author zhouzhenyong
 * @since 2019/1/5 下午1:21
 */
@UtilityClass
public class CollectionUtil {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
