package com.simonalong.mikilin.match;

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
     * @param object 数据对象
     * @param value 待匹配的数据
     * @param name 属性名
     * @return true=匹配成功，false=匹配失败
     */
    boolean match(Object object, String name, Object value);

    /**
     * 判断当前匹配器是否为空
     *
     * @return true=空，false=非空
     */
    boolean isEmpty();

    /**
     * 白名单匹配不上的信息
     *
     * @return 没有匹配上的信息
     */
    String getWhiteMsg();

    /**
     * 黑名单匹配上的信息
     *
     * @return 匹配上的信息
     */
    String getBlackMsg();
}
