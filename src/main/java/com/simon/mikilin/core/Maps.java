package com.simon.mikilin.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzhenyong
 * @since 2018/12/22 下午10:16
 */
public class Maps<K, V> {
    private Map<K, V> dataMap = new HashMap<>();
    public static Maps builder(){
        return new Maps();
    }

    public Maps add(K key, V value){
        dataMap.put(key, value);
        return this;
    }

    public Maps add(Map<K, V> map){
        dataMap.putAll(map);
        return this;
    }

    public Map<K, V> build(){
        return dataMap;
    }
}
