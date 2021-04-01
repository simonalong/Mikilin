package com.simonalong.mikilin.annotation;

import com.simonalong.mikilin.spring.MkAop;
import com.simonalong.mikilin.spring.MkSpringBeanContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 将Mikilin的核查接入spring
 *
 * @author shizi
 * @since 2020-12-01 20:57:33
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({MkSpringBeanContext.class, MkAop.class})
public @interface EnableMikilin {
}
