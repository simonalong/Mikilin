package com.simonalong.mikilin.annotation;

import com.simonalong.mikilin.MkConstant;

import java.lang.annotation.*;

/**
 * 自动核查注解
 *
 * <p> 针对修饰的类和方法中的参数进行核查
 *     <ul>
 *         <li>1.修饰类：则会核查类下面所有函数的所有参数</li>
 *         <li>2.修饰函数：则会核查函数对应的所有参数</li>
 *     </ul>
 * @author shizi
 * @since 2020/6/25 11:20 AM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AutoCheck {

    /**
     * 同{@link AutoCheck#group()}
     * @return 分组
     */
    String value() default MkConstant.DEFAULT_GROUP;

    /**
     * 核查的分组
     * @return 分组
     */
    String group() default MkConstant.DEFAULT_GROUP;
}
