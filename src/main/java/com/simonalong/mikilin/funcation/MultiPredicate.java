package com.simonalong.mikilin.funcation;

/**
 * 添加三个参数的断言函数，用于对{@link java.util.function.BiPredicate}的断言进行多大的扩充
 *
 * @author zhouzhenyong
 * @since 2019-08-12 20:25
 */
@FunctionalInterface
public interface MultiPredicate<T, K, U> {

    boolean test(T t, K k, U u);
}
