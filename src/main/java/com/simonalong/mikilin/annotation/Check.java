package com.simonalong.mikilin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用于标志复杂结构体，用于向内部解析
 * @author zhouzhenyong
 * @since 2019/3/7 下午10:19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {}
