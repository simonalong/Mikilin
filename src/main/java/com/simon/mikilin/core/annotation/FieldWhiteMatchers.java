package com.simon.mikilin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhouzhenyong
 * @since 2019/6/15 上午12:09
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldWhiteMatchers {

    FieldWhiteMatcher[] value();
}
