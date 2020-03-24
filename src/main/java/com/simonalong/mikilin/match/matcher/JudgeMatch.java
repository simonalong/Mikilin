package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.exception.JudgeException;
import com.simonalong.mikilin.funcation.MultiPredicate;
import com.simonalong.mikilin.match.MkContext;
import com.simonalong.mikilin.util.SingleFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统自行判断，对应{@link Matcher#judge()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:52
 */
@Slf4j
public class JudgeMatch extends AbstractBlackWhiteMatch {

    private Predicate<Object> valuePre = null;
    private BiPredicate<Object, Object> objValuePre = null;
    private BiPredicate<Object, Object> valueContextPre = null;
    private MultiPredicate<Object, Object, MkContext> objValueContextPre = null;
    private String judgeStr;
    private MkContext context;

    @Override
    public boolean match(Object object, String name, Object value) {
        if (null != valuePre) {
            if (valuePre.test(value)) {
                setBlackMsg("属性 {0} 的值 {1} 命中禁用条件回调 {2} ", name, value, judgeStr);
                return true;
            } else {
                setWhiteMsg("属性 {0} 的值 {1} 没命中只允许条件回调 {2} ", name, value, judgeStr);
            }
        } else if (null != valueContextPre) {
            if (valueContextPre.test(value, context)) {
                setBlackMsg("属性 {0} 的值 {1} 命中禁用条件回调 {2} ", name, value, judgeStr);
                return true;
            } else {
                setWhiteMsg("属性 {0} 的值 {1} 没命中只允许条件回调 {2} ", name, value, judgeStr);
            }
        } else if (null != objValuePre) {
            if (objValuePre.test(object, value)) {
                setBlackMsg("属性 {0} 的值 {1} 命中禁用条件回调 {2} ", name, value, judgeStr);
                return true;
            } else {
                setWhiteMsg("属性 {0} 的值 {1} 没命中只允许条件回调 {2} ", name, value, judgeStr);
            }
        } else if (null != objValueContextPre) {
            if (objValueContextPre.test(object, value, context)) {
                setBlackMsg("属性 {0} 的值 {1} 命中禁用条件回调 {2} ", name, value, judgeStr);
                return true;
            } else {
                setWhiteMsg("属性 {0} 的值 {1} 没命中只允许条件回调 {2} ", name, value, judgeStr);
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return null == valuePre && null == objValuePre && null == valueContextPre && null == objValueContextPre;
    }

    /**
     * 将一个类中的函数转换为一个系统自定义的过滤匹配器
     *
     * <p>
     * 过滤器可以有多种类型，根据参数的不同有不同的类型
     * <p>
     * @param judge 回调判决，这里是类和对应的函数组成
     * @param context 上下文
     * @return 匹配器的判决器
     */
    public static JudgeMatch build(String judge, MkContext context) {
        if (null == judge || judge.isEmpty() || !judge.contains("#")) {
            return null;
        }

        JudgeMatch judgeMatcher = new JudgeMatch();
        int index = judge.indexOf("#");
        String classStr = judge.substring(0, index);
        String funStr = judge.substring(index + 1);
        // 是否包含函数标志
        AtomicReference<Boolean> containFlag = new AtomicReference<>(false);

        try {
            Class<?> cls = Class.forName(classStr);
            Object object = SingleFactory.getSingle(cls);
            String booleanStr = "boolean";
            judgeMatcher.judgeStr = judge;
            judgeMatcher.context = context;

            // 这里对系统回调支持两种回调方式
            Stream.of(cls.getDeclaredMethods()).filter(m -> m.getName().equals(funStr)).forEach(m -> {
                containFlag.set(true);
                Class<?> returnType = m.getReturnType();
                if (returnType.getSimpleName().equals(Boolean.class.getSimpleName())
                    || returnType.getSimpleName().equals(booleanStr)) {
                    int paramsCnt = m.getParameterCount();
                    // 一个参数，则该参数为属性的类型
                    if (1 == paramsCnt) {
                        judgeMatcher.valuePre = v -> {
                            try {
                                m.setAccessible(true);
                                return (boolean) m.invoke(object, v);
                            } catch (IllegalAccessException | InvocationTargetException ignored) {

                            }
                            return false;
                        };
                    } else if (2 == paramsCnt) {
                        Class<?> p2Cls = m.getParameterTypes()[1];
                        if (MkContext.class.isAssignableFrom(p2Cls)) {
                            // 两个参数，则第一个为核查的对象，第二个为参数为属性的值
                            judgeMatcher.valueContextPre = (v, c) -> {
                                try {
                                    m.setAccessible(true);
                                    return (boolean) m.invoke(object, v, c);
                                } catch (IllegalAccessException | InvocationTargetException ignored) {

                                }
                                return false;
                            };
                        } else {
                            // 两个参数，则第一个为待核查的属性的值，第二个为MkConstext
                            judgeMatcher.objValuePre = (o, v) -> {
                                try {
                                    m.setAccessible(true);
                                    return (boolean) m.invoke(object, o, v);
                                } catch (IllegalAccessException | InvocationTargetException ignored) {

                                }
                                return false;
                            };
                        }
                    } else if (3 == paramsCnt) {
                        Class<?> p3Cls = m.getParameterTypes()[2];
                        // 三个参数，这个时候，第一个参数是核查的对象，第二个参数为属性的值，第三个参数为contexts
                        if(MkContext.class.isAssignableFrom(p3Cls)) {
                            judgeMatcher.objValueContextPre = (o, v, c) -> {
                                try {
                                    m.setAccessible(true);
                                    return (boolean) m.invoke(object, o, v, c);
                                } catch (IllegalAccessException | InvocationTargetException ignored) {

                                }
                                return false;
                            };
                        } else {
                            try {
                                throw new JudgeException("函数"+funStr+"参数匹配失败，三个参数的时候，第三个参数需要为MkContext类型");
                            } catch (JudgeException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        try {
                            throw new JudgeException("函数"+funStr+"的参数匹配失败，最多三个参数");
                        } catch (JudgeException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    try {
                        throw new JudgeException("函数"+funStr+"返回值不是boolean，添加匹配器失败");
                    } catch (JudgeException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                log.error("类{}路径没有找到", classStr, e);
            } else {
                throw new RuntimeException(e);
            }
        }

        if(!containFlag.get()){
            try {
                throw new JudgeException("类"+classStr+"不包含函数" + funStr);
            } catch (JudgeException e) {
                throw new RuntimeException(e);
            }
        }

        return judgeMatcher;
    }
}
