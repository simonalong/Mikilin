package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.function.Predicate;

/**
 * 系统自行判断，对应{@link FieldValidCheck#judge()}或者{@link FieldInvalidCheck#judge()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:52
 */
public class JudgeMatcher implements Matcher {

    private Predicate<Object> predicate;
    private String errMsg;

    @Override
    public boolean match(String name, Object object) {
        if (null != predicate) {
            if (predicate.test(object)) {
                return true;
            } else {
                errMsg = MessageFormat.format("属性[{0}]的值[{1}]在回调中命中", name, object);
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return null == predicate;
    }

    @Override
    public String errMsg() {
        return errMsg;
    }

    /**
     * 将一个类中的函数转换为一个过滤器
     *
     * @return com.xxx.ACls#isValid -> predicate
     */
    @SuppressWarnings("all")
    public JudgeMatcher(Field field, String judge) {
        if (null == judge || judge.isEmpty() || !judge.contains("#")){
            return;
        }
        Integer index = judge.indexOf("#");
        String classStr = judge.substring(0, index);
        String funStr = judge.substring(index + 1);

        try {
            Class<?> cls = Class.forName(classStr);
            Method method = cls.getDeclaredMethod(funStr, field.getType());
            Object object = cls.newInstance();
            Class<?> returnType = method.getReturnType();

            String booleanStr = "boolean";
            if (returnType.getSimpleName().equals(Boolean.class.getSimpleName())
                || returnType.getSimpleName().equals(booleanStr)){
                predicate =  obj -> {
                    try {
                        method.setAccessible(true);
                        return (boolean) method.invoke(object, obj);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return false;
                };
            }
        } catch (ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException e ) {
            e.printStackTrace();
        }
    }

}
