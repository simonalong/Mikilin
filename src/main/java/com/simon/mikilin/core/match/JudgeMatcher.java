package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMather;
import com.simon.mikilin.core.util.SingleFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * 系统自行判断，对应{@link FieldWhiteMather#judge()}或者{@link FieldBlackMatcher#judge()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:52
 */
public class JudgeMatcher extends AbstractBlackWhiteMatcher {

    private Predicate<Object> predicate;
    private String judgeStr;
    private String errMsg;

    @Override
    public boolean match(Object object, String name, Object value) {
        if (null != predicate) {
            if (predicate.test(value)) {
                setBlackMsg("属性[{0}]的值[{1}]命中黑名单回调[{2}]", name, value, judgeStr);
                return true;
            } else {
                setWhiteMsg("属性[{0}]的值[{1}]命中白名单回调[{2}]", name, value, judgeStr);
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return null == predicate;
    }

    /**
     * 将一个类中的函数转换为一个过滤器
     *
     * @param field 属性
     * @param judge 回调判决，这里是类和对应的函数组成
     */
    @SuppressWarnings("all")
    public static JudgeMatcher build(Field field, String judge) {
        if (null == judge || judge.isEmpty() || !judge.contains("#")){
            return null;
        }

        JudgeMatcher judgeMatcher = new JudgeMatcher();
        Integer index = judge.indexOf("#");
        String classStr = judge.substring(0, index);
        String funStr = judge.substring(index + 1);

        try {
            Class<?> cls = Class.forName(classStr);
            Method method = cls.getDeclaredMethod(funStr, field.getType());
            Object object = SingleFactory.getSingle(cls);
            Class<?> returnType = method.getReturnType();

            String booleanStr = "boolean";
            if (returnType.getSimpleName().equals(Boolean.class.getSimpleName())
                || returnType.getSimpleName().equals(booleanStr)){
                judgeMatcher.judgeStr = judge;
                judgeMatcher.predicate =  obj -> {
                    try {
                        method.setAccessible(true);
                        return (boolean) method.invoke(object, obj);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return false;
                };
            }
        } catch (ClassNotFoundException | NoSuchMethodException e ) {
            e.printStackTrace();
        }
        return judgeMatcher;
    }

}
