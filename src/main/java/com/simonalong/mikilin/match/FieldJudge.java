package com.simonalong.mikilin.match;

import com.simonalong.mikilin.CheckDelegate;
import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 属性核查器
 * @author zhouzhenyong
 * @since 2019/4/10 下午12:52
 */
@Getter
@Setter
@Accessors(chain = true)
public class FieldJudge {

    /**
     * 属性名字
     */
    private String name;

    /**
     * 匹配器列表
     */
    private List<Matcher> matcherList = new ArrayList<>();

    /**
     * 属性核查禁用标示，对应{@link FieldWhiteMatcher#disable()}或者{@link FieldBlackMatcher#disable()}
     */
    private Boolean disable;

    /**
     * 判断是否符合匹配器中的白名单匹配
     *
     * 针对匹配器中的所有不空的匹配器进行匹配，如果所有的不为空的白名单中都没有匹配上则说明没有匹配上
     *
     * @param object 待校验的属性的对象
     * @param value 待校验的数据，就是属性的值
     * @param checkDelegate 核查的代理对象
     * @return true：满足任何一个非空白名单，false：不满足任何非空白名单
     */
    public Boolean judgeWhite(Object object, Object value, CheckDelegate checkDelegate) {
        List<String> errMsgList = new ArrayList<>();

        Boolean result = matcherList.stream().filter(m -> !m.isEmpty()).anyMatch(m -> {
            if (m.match(object, name, value)) {
                return true;
            } else {
                if (null != m.getWhiteMsg()) {
                    errMsgList.add(m.getWhiteMsg());
                }
                return false;
            }
        });

        if (result) {
            errMsgList.clear();
            return true;
        }
        checkDelegate.append(errMsgList.toString());
        return false;
    }

    /**
     * 如果所有的不为空的黑名单中是否有任何匹配的
     *
     * 针对匹配器中的所有不空的匹配器进行匹配，如果有任何一个匹配上，则上报失败
     *
     * @param object 待校验的属性的对象
     * @param value 待校验的属性的值
     * @param checkDelegate 核查的代理对象
     * @return true：满足任何一个黑名单，false：所有黑名单都不满足
     */
    public Boolean judgeBlack(Object object, Object value, CheckDelegate checkDelegate) {
        AtomicReference<String> errMsg = new AtomicReference<>();
        Boolean result = matcherList.stream().filter(m -> !m.isEmpty()).anyMatch(m -> {
            if (m.match(object, name, value)) {
                errMsg.set(m.getBlackMsg());
                return true;
            }
            return false;
        });

        if (result) {
            checkDelegate.append(errMsg.get());
            return true;
        }
        return false;
    }

    /**
     * 判断是否有匹配器不空，如果有任何一个匹配器不空，则可以启动属性判决
     *
     * @return true：条件为空，false：条件不空
     */
    public Boolean isEmpty() {
        if (disable) {
            return true;
        }

        return matcherList.stream().allMatch(Matcher::isEmpty);
    }

    public static FieldJudge buildFromValid(Field field, FieldWhiteMatcher validCheck) {
        return new FieldJudge()
            .setName(field.getName())
            .addMatcher(ValueMather.build(field, validCheck.value()))
            .addMatcher(MatcherFactory.build(TypeMatcher.class, validCheck.type()))
            .addMatcher(MatcherFactory.build(EnumTypeMatcher.class, validCheck.enumType()))
            .addMatcher(MatcherFactory.build(RangeMatcher.class, validCheck.range()))
            .addMatcher(MatcherFactory.build(ConditionMatcher.class, validCheck.condition()))
            .addMatcher(MatcherFactory.build(RegexMatcher.class, validCheck.regex()))
            .addMatcher(JudgeMatcher.build(field, validCheck.judge()))
            .setDisable(validCheck.disable());
    }

    public static FieldJudge buildFromInvalid(Field field, FieldBlackMatcher invalidCheck) {
        return new FieldJudge()
            .setName(field.getName())
            .addMatcher(ValueMather.build(field, invalidCheck.value()))
            .addMatcher(MatcherFactory.build(TypeMatcher.class, invalidCheck.type()))
            .addMatcher(MatcherFactory.build(EnumTypeMatcher.class, invalidCheck.enumType()))
            .addMatcher(MatcherFactory.build(RangeMatcher.class, invalidCheck.range()))
            .addMatcher(MatcherFactory.build(ConditionMatcher.class, invalidCheck.condition()))
            .addMatcher(MatcherFactory.build(RegexMatcher.class, invalidCheck.regex()))
            .addMatcher(JudgeMatcher.build(field, invalidCheck.judge()))
            .setDisable(invalidCheck.disable());
    }

    private FieldJudge addMatcher(Matcher matcher) {
        if (null != matcher) {
            matcherList.add(matcher);
        }
        return this;
    }
}
