package com.simon.mikilin.core.annotation;

import com.simon.mikilin.core.FieldEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhouzhenyong
 * @since 2019/3/7 下午9:47
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldIncludeCheck {

    /**
     * 可用的值
     * 如果允许值为null，那么添加一个排除的值为"null"，因为不允许直接设置为null
     */
    String[] value() default {};

    /**
     * 可用的值对应的类型
     */
    FieldEnum type() default FieldEnum.DEFAULT;

    /**
     * 是否可用
     */
    boolean disable() default false;
}
