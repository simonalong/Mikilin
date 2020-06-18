package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.exception.MkException;

import java.lang.reflect.Field;

/**
 * 拦截null和空字符
 *
 * @author shizi
 * @since 2020/6/18 12:20 PM
 */
public class NotBlankMatch extends AbstractBlackWhiteMatch {

    private Boolean notBlank;

    public static NotBlankMatch build(Field field, String notBlankStr) {
        NotBlankMatch notBlankMatch = new NotBlankMatch();
        if (!"".equals(notBlankStr)) {
            notBlankMatch.notBlank = Boolean.parseBoolean(notBlankStr);
        } else {
            return notBlankMatch;
        }

        if (!CharSequence.class.isAssignableFrom(field.getType())) {
            throw new MkException("类型不匹配：匹配器属性【notBlank】不能修饰类型" + field.getType().getCanonicalName());
        }

        return notBlankMatch;
    }

    @Override
    public boolean match(Object object, String name, Object value) {
        if (null == value) {
            if (notBlank) {
                setWhiteMsg("属性 {0} 的值为null", name);
                return false;
            } else {
                setBlackMsg("属性 {0} 的值为null", name);
                return true;
            }
        }
        if (!(value instanceof CharSequence)) {
            setWhiteMsg("属性 {0} 的值不是字符类型", name);
            return false;
        }

        if (notBlank) {
            if (!"".equals(value)) {
                setBlackMsg("属性 {0} 的值为非空字符", name);
                return true;
            } else {
                setBlackMsg("属性 {0} 的值为空字符", name);
                return true;
            }
        } else {
            if ("".equals(value)) {
                setBlackMsg("属性 {0} 的值为空字符", name);
                return true;
            } else {
                setWhiteMsg("属性 {0} 的值不为空", name);
                return false;
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return (null == notBlank);
    }
}
