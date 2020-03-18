package com.simonalong.mikilin.match;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.matcher.*;

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
    private List<Match> matchList = new ArrayList<>();

    /**
     * 拦截后的自定义描述
     */
    private String errMsg;

    /**
     * 属性核查禁用标示，对应{@link Matcher#disable()}
     */
    private Boolean disable;

    /**
     * 判断是否符合匹配器中的白名单匹配
     * <p>
     * 针对匹配器中的所有不空的匹配器进行匹配，如果所有的不为空的白名单中都没有匹配上则说明没有匹配上
     *
     * @param object  待校验的属性的对象
     * @param value   待校验的数据，就是属性的值
     * @param context 核查上下文
     * @return true：满足任何一个非空白名单，false：不满足任何非空白名单
     */
    public Boolean judgeWhite(Object object, Object value, MkContext context) {
        List<String> errMsgList = new ArrayList<>();
        long whiteMatchCount = matchList.stream().filter(Match::isNotEmpty).filter(m -> {
            if (m.match(object, name, value)) {
                return true;
            } else {
                String whiteErrMsg = m.getWhiteMsg();
                if (null != whiteErrMsg) {
                    errMsgList.add(whiteErrMsg);
                    if (!"".equals(errMsg)) {
                        context.setLastErrMsg(errMsg);
                    } else {
                        context.setLastErrMsg(m.getBlackMsg());
                    }
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
     * <p>
     * 针对匹配器中的所有不空的匹配器进行匹配，如果有任何一个匹配上，则上报失败
     *
     * @param object  待校验的属性的对象
     * @param value   待校验的属性的值
     * @param context 核查上下文
     * @return true：满足任何一个黑名单，false：所有黑名单都不满足
     */
    public Boolean judgeBlack(Object object, Object value, MkContext context) {
        List<String> errMsgList = new ArrayList<>();
        long blackMatchCount = matchList.stream().filter(Match::isNotEmpty).filter(m -> {
            if (m.match(object, name, value)) {
                errMsgList.add(m.getBlackMsg());
                if (!"".equals(errMsg)) {
                    context.setLastErrMsg(errMsg);
                } else {
                    context.setLastErrMsg(m.getBlackMsg());
                }
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

        return matchList.stream().allMatch(Match::isEmpty);
    }

    @SuppressWarnings("all")
    public static FieldJudge buildFromValid(Field field, Matcher validCheck, MkContext context) {
        return new FieldJudge().setName(field.getName())
            .addMatcher(TypeMatch.build(validCheck.type()))
            .addMatcher(ValueMather.build(field, validCheck.value()))
            .addMatcher(MatcherFactory.build(ModelMatch.class, validCheck.model()))
            .addMatcher(MatcherFactory.build(EnumTypeMatch.class, validCheck.enumType()))
            .addMatcher(MatcherFactory.build(RangeMatch.class, validCheck.range()))
            .addMatcher(ConditionMatch.build(field, validCheck.condition()))
            .addMatcher(MatcherFactory.build(RegexMatch.class, validCheck.regex()))
            .addMatcher(JudgeMatch.build(validCheck.judge(), context))
            .setErrMsg(validCheck.errMsg())
            .setDisable(validCheck.disable());
    }

    private FieldJudge addMatcher(Match match) {
        if (null != match) {
            matchList.add(match);
        }
        return this;
    }
}
