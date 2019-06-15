package com.github.simonalong.mikilin.match;

/**
 * @author zhouzhenyong
 * @since 2019/4/11 下午9:07
 */
public interface Builder<T, K> {

    /**
     * 构造器模式，用于构造对应的结构数据
     *
     * @param obj 待构造需要的数据
     * @return 构造出来的数据
     */
    T build(K obj);
}
