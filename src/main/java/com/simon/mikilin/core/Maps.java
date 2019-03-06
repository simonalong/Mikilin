package com.simon.mikilin.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzhenyong
 * @since 2018/12/22 下午10:16
 */
class Maps<K, V> {
    private Map<K, V> dataMap = new HashMap<>();
    static Maps builder(){
        return new Maps();
    }

    Maps add(K key, V value){
        dataMap.put(key, value);
        return this;
    }

    Maps add(Map<K, V> map){
        dataMap.putAll(map);
        return this;
    }

    Map<K, V> build(){
        return dataMap;
    }
}
