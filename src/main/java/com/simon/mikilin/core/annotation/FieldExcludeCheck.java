package com.simon.mikilin.core.annotation;

import com.simon.mikilin.core.FieldEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.Data;

/**
 * 下面的所有类型校验核查都是可以允许的
 *
 * @author zhouzhenyong
 * @since 2019/3/7 下午9:50
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldExcludeCheck {

    /**
     * 禁用的值
     * 如果允许值为null，那么添加一个排除的值为"null"，因为不允许直接设置为null
     */
    String[] value() default {};

    /**
     * 可用的值对应的类型
     */
    FieldEnum type() default FieldEnum.DEFAULT;

    /**
     * 内部类的判断的调用
     * 比如："com.xxx.AEntity#isValid"，其中#后面是方法，方法返回boolean或者包装类，如参为当前修饰的Field的类型或者子类
     */
    String judge() default "";

    /**
     * 是否可用
     */
    boolean disable() default false;
}
