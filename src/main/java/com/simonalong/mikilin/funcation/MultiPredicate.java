package com.simonalong.mikilin.funcation;

/**
 * 添加三个参数的断言函数，用于对{@link java.util.function.BiPredicate}的断言进行多大的扩充
 *
 * @author zhouzhenyong
 * @since 2019-08-12 20:25
 */
@FunctionalInterface
public interface MultiPredicate<T, K, U> {

    /**
     * 对给定的参数进行测量
     * @param t 第一个参数
     * @param k 第二个参数
     * @param u 第三个参数
     * @return 如果参数匹配则返回 {@code true} 否则返回{@code false}
     */
    boolean test(T t, K k, U u);
}
