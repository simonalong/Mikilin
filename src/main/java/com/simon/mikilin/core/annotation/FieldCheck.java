package com.simon.mikilin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhouzhenyong
 * @since 2018/12/20 下午1:58
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldCheck {

    /**
     * 可用的值
     */
    String[] includes() default {};

    /**
     * 不可用的值，只有includes 为空才会判断下面这个
     */
    String[] excludes() default {};

    boolean disable() default false;
}
