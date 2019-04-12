package com.simon.mikilin.core.match;

/**
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:45
 */
public interface Matcher {

    /**
     * 判断是否匹配
     *
     * 只有isEmpty为false时候才会匹配
     *
     * @param object 待匹配的数据
     * @param name 属性名
     * @return true=匹配成功，false=匹配失败
     */
    boolean match(String name, Object object);

    /**
     * 判断当前匹配器是否为空
     *
     * @return true=空，false=非空
     */
    boolean isEmpty();

    /**
     * 如果没有匹配上，则返回对应的信息
     *
     * @return 没有匹配上的信息
     */
    String errMsg();
}
