package com.simonalong.mikilin.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author shizi
 * @since 2020/6/25 11:45 AM
 */
public class MkSpringBeanContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MkSpringBeanContext.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name){
        if (null != applicationContext) {
            return applicationContext.getBean(name);
        }
        return null;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (null != applicationContext) {
            return applicationContext.getBean(clazz);
        }
        return null;
    }
}
