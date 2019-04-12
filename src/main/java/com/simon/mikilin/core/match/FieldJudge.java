package com.simon.mikilin.core.match;

import com.simon.mikilin.core.CheckDelegate;
import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
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
     * 属性核查禁用标示，对应{@link FieldValidCheck#disable()}或者{@link FieldInvalidCheck#disable()}
     */
    private Boolean disable;

    /**
     * 判断是否符合匹配器中的匹配
     *
     * 针对匹配器中的所有不空的匹配器进行匹配
     *
     * @param object 待校验的数据
     * @return true：匹配上，false：没有匹配上
     */
    public Boolean judge(Object object, CheckDelegate checkDelegate) {
        AtomicReference<Boolean> result = new AtomicReference<>(false);
        List<String> errMsgList = new ArrayList<>();

        matcherList.stream().filter(m -> !m.isEmpty()).forEach(m -> {
            if (!m.match(name, object)) {
                if (null != m.errMsg()) {
                    errMsgList.add(m.errMsg());
                }
            } else {
                result.set(true);
            }
        });

        if (result.get()) {
            errMsgList.clear();
            return true;
        }
        checkDelegate.append(errMsgList.toString());
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

    public static FieldJudge buildFromValid(Field field, FieldValidCheck validCheck) {
        return new FieldJudge()
            .setName(field.getName())
            .addMatcher(new ValueMather(field, validCheck.value()))
            .addMatcher(MatcherFactory.build(TypeMatcher.class, validCheck.type()))
            .addMatcher(MatcherFactory.build(EnumTypeMatcher.class, validCheck.enumType()))
            .addMatcher(MatcherFactory.build(RangeMatcher.class, validCheck.range()))
            .addMatcher(MatcherFactory.build(ConditionMatcher.class, validCheck.condition()))
            .addMatcher(MatcherFactory.build(RegexMatcher.class, validCheck.regex()))
            .addMatcher(new JudgeMatcher(field, validCheck.judge()))
            .setDisable(validCheck.disable());
    }

    public static FieldJudge buildFromInvalid(Field field, FieldInvalidCheck invalidCheck) {
        return new FieldJudge()
            .setName(field.getName())
            .addMatcher(new ValueMather(field, invalidCheck.value()))
            .addMatcher(MatcherFactory.build(TypeMatcher.class, invalidCheck.type()))
            .addMatcher(MatcherFactory.build(EnumTypeMatcher.class, invalidCheck.enumType()))
            .addMatcher(MatcherFactory.build(RangeMatcher.class, invalidCheck.range()))
            .addMatcher(MatcherFactory.build(ConditionMatcher.class, invalidCheck.condition()))
            .addMatcher(MatcherFactory.build(RegexMatcher.class, invalidCheck.regex()))
            .addMatcher(new JudgeMatcher(field, invalidCheck.judge()))
            .setDisable(invalidCheck.disable());
    }

    private FieldJudge addMatcher(Matcher matcher) {
        if (null != matcher) {
            matcherList.add(matcher);
        }
        return this;
    }
}
