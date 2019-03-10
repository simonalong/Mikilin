package com.simon.mikilin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 修饰基本的属性（Boolean Byte Character Short Integer Long Double Float）和 String类型，属性的所有可用的值
 * @author zhouzhenyong
 * @since 2019/3/7 下午9:47
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldValidCheck {

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
     * 禁用的值对应的类型
     */
    String regex() default "";

    /**
     * 内部类的判断的调用
     * 比如："com.xxx.AEntity#isValid"，其中#后面是方法，方法返回boolean或者包装类，入参为当前Field对应的类型或者子类
     */
    String judge() default "";

    /**
     * 是否不可用
     */
    boolean disable() default false;
}
