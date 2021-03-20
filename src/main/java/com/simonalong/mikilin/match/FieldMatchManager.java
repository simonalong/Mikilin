package com.simonalong.mikilin.match;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.exception.MkException;
import com.simonalong.mikilin.match.matcher.*;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 属性匹配管理器
 *
 * @author zhouzhenyong
 * @since 2019/4/10 下午12:52
 */
@Getter
@Setter
@Accessors(chain = true)
public class FieldMatchManager {

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
     * 属性匹配匹配器
     *
     * @param object 待校验的属性的对象
     * @param value 待校验的数据，就是属性的值
     * @param context 核查上下文
     * @param whiteOrBlack 黑白名单类型
     * @return true：匹配任何一个匹配器返回true，false：所有匹配器都没有匹配上
     */
    public Boolean match(Object object, Object value, MkContext context, Boolean whiteOrBlack) {
        List<String> errMsgList = new ArrayList<>();
        for (Match m : matchList) {
            if (null == m || m.isEmpty()) {
                continue;
            }

            if (m.match(object, name, value)) {
                if (!whiteOrBlack) {
                    context.append(m.getBlackMsg());
                    setLastErrMsg(object, context, m.getBlackMsg(), value, name);
                } else {
                    context.clear();
                }
                return true;
            } else {
                if(whiteOrBlack) {
                    errMsgList.add(m.getWhiteMsg());
                    setLastErrMsg(object, context, m.getWhiteMsg(), value, name);
                }
            }
        }

        if (whiteOrBlack) {
            context.append(errMsgList);
        }

        return false;
    }

    public Boolean match(Object value, MkContext context, Boolean whiteOrBlack) {
        List<String> errMsgList = new ArrayList<>();
        for (Match m : matchList) {
            if (null == m || m.isEmpty()) {
                continue;
            }

            if (m.match(null, name, value)) {
                if (!whiteOrBlack) {
                    context.append(m.getBlackMsg());
                    setLastErrMsg(null, context, m.getBlackMsg(), value, name);
                } else {
                    context.clear();
                }
                return true;
            } else {
                if(whiteOrBlack) {
                    errMsgList.add(m.getWhiteMsg());
                    setLastErrMsg(null, context, m.getWhiteMsg(), value, name);
                }
            }
        }

        if (whiteOrBlack) {
            context.append(errMsgList);
        }

        return false;
    }

    private void setLastErrMsg(Object object, MkContext context, String sysErrMsg, Object value, String fieldName) {
        if (null != sysErrMsg) {
            if (!"".equals(errMsg)) {
                if (null == context.getLastErrMsg()) {
                    context.setLastErrMsg("属性(" + fieldName + ")核查失败：" + parseErrMsg(object, value));
                }
            } else {
                if (null == context.getLastErrMsg()) {
                    context.setLastErrMsg("属性(" + fieldName + ")核查失败：" + sysErrMsg);
                }
            }
        }
    }

    private String parseErrMsg(Object object, Object value) {
        String result = errMsg;
        if (null != object) {
            String regex = "(#root)\\.(\\w+)";
            java.util.regex.Matcher m = Pattern.compile(regex).matcher(errMsg);
            while (m.find()) {
                String fieldName = m.group(2);
                Object fieldValue = getFieldValue(fieldName, object);

                result = errMsg.replaceAll("#root." + fieldName, fieldValue.toString());
            }
        }

        if(null != value) {
            result = result.replaceAll("#current", value.toString());
        } else {
            result = result.replaceAll("#current", "null");
        }
        return result;
    }

    private Object getFieldValue(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new MkException(e);
        }
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

    public static FieldMatchManager buildFromValid(Object value, Matcher validCheck, MkContext context) {
        FieldMatchManager matchManager = new FieldMatchManager()
            .addMatcher(MatcherFactory.build(ModelMatch.class, validCheck.model()))
            .addMatcher(MatcherFactory.build(EnumTypeMatch.class, validCheck.enumType()))
            .addMatcher(MatcherFactory.build(RangeMatch.class, validCheck.range()))
            .addMatcher(MatcherFactory.build(RegexMatch.class, validCheck.regex()))
            .addMatcher(MatcherFactory.build(NotNullMatch.class, validCheck.notNull()))
            .setErrMsg(validCheck.errMsg())
            .setDisable(validCheck.disable());

        if (value instanceof Field) {
            Field field = (Field) value;
            return matchManager.setName(field.getName())
                .addMatcher(TypeMatch.build(field, validCheck.type()))
                .addMatcher(ValueMath.build(field, validCheck.value()))
                .addMatcher(ConditionMatch.build(field.getName(), validCheck.condition()))
                .addMatcher(CustomizeMatch.build(field, validCheck.customize(), context))
                .addMatcher(NotBlankMatch.build(field.getType(), validCheck.notBlank()));
        } else if (value instanceof Parameter) {
            Parameter parameter = (Parameter) value;
            return matchManager.setName(parameter.getName())
                .addMatcher(TypeMatch.build(parameter, validCheck.type()))
                .addMatcher(ValueMath.build(parameter, validCheck.value()))
                .addMatcher(ConditionMatch.build(parameter.getName(), validCheck.condition()))
                .addMatcher(CustomizeMatch.build(parameter, validCheck.customize(), context))
                .addMatcher(NotBlankMatch.build(parameter.getType(), validCheck.notBlank()));
        } else {
            return matchManager;
        }
    }

    private FieldMatchManager addMatcher(Match match) {
        if (null != match) {
            matchList.add(match);
        }
        return this;
    }
}
