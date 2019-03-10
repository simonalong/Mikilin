package com.simon.mikilin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 修饰基本类型可以添加黑白名单，但是，自定义、集合和Map类型不需要添加黑白名单，只是用于路径检索
 * @author zhouzhenyong
 * @since 2018/12/20 下午1:58
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldCheck {

    /**
     * 可用的值
     * 如果允许值为null，那么添加一个排除的值为"null"，因为不允许直接设置为null
     */
    String[] includes1() default {};

    /**
     * 禁用的值
     * 如果不允许值为null，那么添加一个排除的值为"null"，因为不允许直接设置为null
     */
    String[] excludes1() default {};

    /**
     * 可用的值对应的类型
     */
    FieldEnum includeType() default FieldEnum.DEFAULT;

    /**
     * 禁用的值对应的类型
     */
    FieldEnum excludeType() default FieldEnum.DEFAULT;

    boolean disable() default false;
}
