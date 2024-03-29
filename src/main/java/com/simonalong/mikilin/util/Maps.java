package com.simonalong.mikilin.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.simonalong.mikilin.MkConstant.MK_LOG_PRE;

/**
 * @author zhouzhenyong
 * @since 2018/12/22 下午10:16
 */
@Slf4j
public final class Maps<K, V> implements Serializable {

    private static final Integer KV_NUM = 2;
    private Maps() {}

    @Getter
    private final Map<K, V> dataMap = new HashMap<>();

    /**
     * key-value-key-value...这种格式初始化map
     *
     * @param kvs key-value-key-value这种kv入参
     * @return 构造的Maps结构
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Maps of(Object... kvs) {
        if (kvs.length % KV_NUM != 0) {
            log.error(MK_LOG_PRE + "Maps.of的参数需要是key-value-key-value...这种格式");
            return new Maps();
        }

        Maps maps = new Maps();
        for (int i = 0; i < kvs.length; i += KV_NUM) {
            if (null == kvs[i]) {
                log.error(MK_LOG_PRE + "map的key不可以为null");
                return maps;
            }
            maps.put(kvs[i], kvs[i + 1]);
        }
        return maps;
    }

    public Maps<K, V> put(Map<K, V> map) {
        dataMap.putAll(map);
        return this;
    }

    public Maps<K, V> put(K key, V value) {
        dataMap.put(key, value);
        return this;
    }

    public Maps<K, V> add(Map<K, V> map) {
        dataMap.putAll(map);
        return this;
    }

    public Maps<K, V> add(K key, V value) {
        dataMap.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return dataMap;
    }

    @Override
    public String toString(){
        return dataMap.toString();
    }
}
