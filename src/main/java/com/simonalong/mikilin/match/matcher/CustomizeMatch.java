package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.MkConstant;
import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.exception.JudgeException;
import com.simonalong.mikilin.match.MkContext;
import com.simonalong.mikilin.util.SingleFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统自行判断，对应{@link Matcher#customize()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:52
 */
@Slf4j
public class CustomizeMatch extends AbstractBlackWhiteMatch {

    @Setter
    private Method targetMethod;
    private String judgeStr;
    private MkContext context;

    /**
     * 所属类的类型
     */
    @Setter
    @Getter
    private Class<?> belongToObjectClass;
    /**
     * 属性的类型
     */
    @Setter
    @Getter
    private Class<?> fieldClass;
    @Setter
    @Getter
    private Object methodCallObject;


    @Override
    public boolean match(Object object, String name, Object value) {
        List<Object> parameterValueList = new ArrayList<>();
        for (Class<?> parameterType : targetMethod.getParameterTypes()) {
            if (MkContext.class.isAssignableFrom(parameterType)) {
                parameterValueList.add(context);
            } else if (belongToObjectClass.isAssignableFrom(parameterType)) {
                parameterValueList.add(object);
            } else if (fieldClass.isAssignableFrom(parameterType)) {
                parameterValueList.add(value);
            }
        }

        try {
            targetMethod.setAccessible(true);
            Boolean result = (Boolean) targetMethod.invoke(methodCallObject, parameterValueList.toArray());
            if (result) {
                setBlackMsg("属性 {0} 的值 {1} 命中禁用条件回调 {2} ", name, value, judgeStr);
            } else {
                setWhiteMsg("属性 {0} 的值 {1} 没命中只允许条件回调 {2} ", name, value, judgeStr);
            }
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(MkConstant.LOG_PRE + "函数：{}执行失败", targetMethod.getName(), e);
        }

        setWhiteMsg("属性 {0} 的值 {1} 没命中只允许条件回调 {2} ", name, value, judgeStr);
        return false;
    }

    @Override
    public boolean isEmpty() {
        return null == targetMethod;
    }

    /**
     * 将一个类中的函数转换为一个系统自定义的过滤匹配器
     *
     * <p>
     * 过滤器可以有多种类型，根据参数的不同有不同的类型
     * <p>
     *
     * @param field   属性
     * @param judge   回调判决，这里是类和对应的函数组成
     * @param context 上下文
     * @return 匹配器的判决器
     */
    public static CustomizeMatch build(Field field, String judge, MkContext context) {
        if (null == judge || judge.isEmpty() || !judge.contains("#")) {
            return null;
        }

        CustomizeMatch customizeMatch = new CustomizeMatch();
        int index = judge.indexOf("#");
        String classStr = judge.substring(0, index);
        String funStr = judge.substring(index + 1);

        try {
            Class<?> cls = Class.forName(classStr);
            customizeMatch.setBelongToObjectClass(field.getDeclaringClass());
            customizeMatch.setFieldClass(field.getType());

            Object object = SingleFactory.getSingle(cls);
            String booleanStr = "boolean";
            customizeMatch.setMethodCallObject(object);
            customizeMatch.judgeStr = judge;
            customizeMatch.context = context;


            // 这里不支持函数重载，一个函数只限定使用一个最后一个包含对应三种类型的函数
            Method method = Stream.of(cls.getDeclaredMethods()).filter(m -> {
                // 过滤不是所需函数
                if (!m.getName().equals(funStr)) {
                    return false;
                }

                // 过滤返回值非boolean类型
                Class<?> returnType = m.getReturnType();
                if (!returnType.getSimpleName().equals(Boolean.class.getSimpleName()) && !returnType.getSimpleName().equals(booleanStr)) {
                    return false;
                }

                // 过滤包含除了三类型之外其他类型的函数
                return containTargetParameterType(m, customizeMatch.getBelongToObjectClass(), customizeMatch.getFieldClass());
            }).findFirst().orElse(null);

            if (null == method) {
                throw new JudgeException("没有找到函数为：" + funStr + "，返回值为boolean，包含三种类型（MkConstant或参数所属类型或参数类型）的函数");
            }

            customizeMatch.setTargetMethod(method);
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                log.error("类{}路径没有找到", classStr, e);
            } else {
                throw new RuntimeException(e);
            }
        }
        return customizeMatch;
    }


    /**
     * 函数是否只包含所需类型：一旦包含非所需类型，则返回false
     *
     * @param method             回调函数
     * @param fieldBelongToClass 参数所属对象的类型
     * @param fieldTypeClass     参数的类型
     * @return true: 只包含所需类型，false：包含非所需类型
     */
    private static boolean containTargetParameterType(Method method, Class<?> fieldBelongToClass, Class<?> fieldTypeClass) {
        if (method.getParameterTypes().length > 3) {
            return false;
        }
        for (Class<?> parameterType : method.getParameterTypes()) {
            // Object类，则认为不匹配，因为无法明确这个参数代表的是哪个类型
            if (parameterType.equals(Object.class)) {
                return false;
            }

            if (parameterType.isAssignableFrom(fieldBelongToClass) || parameterType.isAssignableFrom(fieldTypeClass) || parameterType.isAssignableFrom(MkContext.class)) {
                continue;
            }
            return false;
        }
        return true;
    }
}
