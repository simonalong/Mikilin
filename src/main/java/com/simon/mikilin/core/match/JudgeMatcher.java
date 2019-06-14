package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMatcher;
import com.simon.mikilin.core.util.SingleFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统自行判断，对应{@link FieldWhiteMatcher#judge()}或者{@link FieldBlackMatcher#judge()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:52
 */
@Slf4j
public class JudgeMatcher extends AbstractBlackWhiteMatcher {

    private BiPredicate<Object, Object> biPredicate = null;
    private Predicate<Object> predicate = null;
    private String judgeStr;

    @Override
    public boolean match(Object object, String name, Object value) {
        if (null != predicate) {
            if (predicate.test(value)) {
                setBlackMsg("属性[{0}]的值[{1}]命中黑名单回调[{2}]", name, value, judgeStr);
                return true;
            } else {
                setWhiteMsg("属性[{0}]的值[{1}]命中白名单回调[{2}]", name, value, judgeStr);
            }
        } else if (null != biPredicate) {
            if (biPredicate.test(object, value)) {
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
        return null == predicate && null == biPredicate;
    }

    /**
     * 将一个类中的函数转换为一个过滤器
     *
     * @param field 属性
     * @param judge 回调判决，这里是类和对应的函数组成
     */
    @SuppressWarnings("all")
    public static JudgeMatcher build(Field field, String judge) {
        if (null == judge || judge.isEmpty() || !judge.contains("#")) {
            return null;
        }

        JudgeMatcher judgeMatcher = new JudgeMatcher();
        Integer index = judge.indexOf("#");
        String classStr = judge.substring(0, index);
        String funStr = judge.substring(index + 1);

        try {
            Class<?> cls = Class.forName(classStr);
            Object object = SingleFactory.getSingle(cls);
            String booleanStr = "boolean";
            judgeMatcher.judgeStr = judge;
            // 这里对系统回调支持两种回调方式
            Stream.of(cls.getDeclaredMethods()).filter(m -> m.getName().equals(funStr)).forEach(m -> {
                Class<?> returnType = m.getReturnType();
                if (returnType.getSimpleName().equals(Boolean.class.getSimpleName())
                    || returnType.getSimpleName().equals(booleanStr)) {
                    Integer paramsCnt = m.getParameterCount();
                    // 一个参数，则该参数为属性的类型
                    if (1 == paramsCnt) {
                        judgeMatcher.predicate = f -> {
                            try {
                                m.setAccessible(true);
                                return (boolean) m.invoke(object, f);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return false;
                        };
                    } else if (2 == paramsCnt) {
                        // 两个参数，则第一个为核查的对象的值，第二个为当前修饰的属性的值
                        judgeMatcher.biPredicate = (obj, f) -> {
                            try {
                                m.setAccessible(true);
                                return (boolean) m.invoke(object, obj, f);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return false;
                        };
                    }
                } else {
                    log.error("函数{}返回值不是boolean，添加匹配器失败");
                }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return judgeMatcher;
    }
}
