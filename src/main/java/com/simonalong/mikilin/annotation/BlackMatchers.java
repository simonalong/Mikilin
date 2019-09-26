package com.simonalong.mikilin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhouzhenyong
 * @since 2019/6/14 下午11:49
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BlackMatchers {

    BlackMatcher[] value();
}
