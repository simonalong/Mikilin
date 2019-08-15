package com.simonalong.mikilin.match;

import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.match.matcher.ConditionMatcher;
import com.simonalong.mikilin.match.matcher.EnumTypeMatcher;
import com.simonalong.mikilin.match.matcher.JudgeMatcher;
import com.simonalong.mikilin.match.matcher.Matcher;
import com.simonalong.mikilin.match.matcher.MatcherFactory;
import com.simonalong.mikilin.match.matcher.RangeMatcher;
import com.simonalong.mikilin.match.matcher.RegexMatcher;
import com.simonalong.mikilin.match.matcher.ModelMatcher;
import com.simonalong.mikilin.match.matcher.ValueMather;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 属性核查器
 *
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
     * @param context 核查上下文
     * @return true：满足任何一个非空白名单，false：不满足任何非空白名单
     */
    public Boolean judgeWhite(Object object, Object value, MkContext context) {
        List<String> errMsgList = new ArrayList<>();
        Long whiteMatchCount = matcherList.stream().filter(Matcher::isNotEmpty)
            .filter(m -> {
                if (m.match(object, name, value)) {
                    return true;
                } else {
                    if (null != m.getWhiteMsg()) {
                        errMsgList.add(m.getWhiteMsg());
                    }
                    return false;
                }
            }).count();

        if (0 == whiteMatchCount) {
            context.append(errMsgList);
            return false;
        }

        errMsgList.clear();
        return true;
    }

    /**
     * 如果所有的不为空的黑名单中是否有任何匹配的
     *
     * 针对匹配器中的所有不空的匹配器进行匹配，如果有任何一个匹配上，则上报失败
     *
     * @param object 待校验的属性的对象
     * @param value 待校验的属性的值
     * @param context 核查上下文
     * @return true：满足任何一个黑名单，false：所有黑名单都不满足
     */
    public Boolean judgeBlack(Object object, Object value, MkContext context) {
        List<String> errMsgList = new ArrayList<>();
        Long blackMatchCount = matcherList.stream().filter(Matcher::isNotEmpty)
            .filter(m -> {
                if (m.match(object, name, value)) {
                    errMsgList.add(m.getBlackMsg());
                    return true;
                }
                return false;
            }).count();

        if (0 == blackMatchCount) {
            return false;
        }

        context.append(errMsgList);
        return true;
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

    public static FieldJudge buildFromValid(Field field, FieldWhiteMatcher validCheck, MkContext context) {
        return new FieldJudge()
            .setName(field.getName())
            .addMatcher(ValueMather.build(field, validCheck.value()))
            .addMatcher(MatcherFactory.build(ModelMatcher.class, validCheck.model()))
            .addMatcher(MatcherFactory.build(EnumTypeMatcher.class, validCheck.enumType()))
            .addMatcher(MatcherFactory.build(RangeMatcher.class, validCheck.range()))
            .addMatcher(ConditionMatcher.build(field, validCheck.condition()))
            .addMatcher(MatcherFactory.build(RegexMatcher.class, validCheck.regex()))
            .addMatcher(JudgeMatcher.build(field, validCheck.judge(), context))
            .setDisable(validCheck.disable());
    }

    public static FieldJudge buildFromInvalid(Field field, FieldBlackMatcher invalidCheck, MkContext context) {
        return new FieldJudge()
            .setName(field.getName())
            .addMatcher(ValueMather.build(field, invalidCheck.value()))
            .addMatcher(MatcherFactory.build(ModelMatcher.class, invalidCheck.model()))
            .addMatcher(MatcherFactory.build(EnumTypeMatcher.class, invalidCheck.enumType()))
            .addMatcher(MatcherFactory.build(RangeMatcher.class, invalidCheck.range()))
            .addMatcher(ConditionMatcher.build(field, invalidCheck.condition()))
            .addMatcher(MatcherFactory.build(RegexMatcher.class, invalidCheck.regex()))
            .addMatcher(JudgeMatcher.build(field, invalidCheck.judge(), context))
            .setDisable(invalidCheck.disable());
    }

    private FieldJudge addMatcher(Matcher matcher) {
        if (null != matcher) {
            matcherList.add(matcher);
        }
        return this;
    }
}
