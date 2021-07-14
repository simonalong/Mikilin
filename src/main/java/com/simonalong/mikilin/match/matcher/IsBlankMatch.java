package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.exception.MkException;

/**
 * 拦截null和空字符，指定的类型判断，对应{@link Matcher#isBlank()}
 *
 * @author shizi
 * @since 2020/6/18 12:20 PM
 */
public class IsBlankMatch extends AbstractBlackWhiteMatch {

    private Boolean isBlank;

    public static IsBlankMatch build(Class<?> typeClass, String notBlankStr) {
        IsBlankMatch notBlankMatch = new IsBlankMatch();
        if (null != notBlankStr && !"".equals(notBlankStr)) {
            notBlankMatch.isBlank = Boolean.parseBoolean(notBlankStr);
        } else {
            return notBlankMatch;
        }

        if (!CharSequence.class.isAssignableFrom(typeClass)) {
            throw new MkException("类型不匹配：匹配器属性【notBlank】不能修饰类型" + typeClass.getCanonicalName());
        }

        return notBlankMatch;
    }

    @Override
    public boolean match(Object object, String name, Object value) {
        if (null == value) {
            if (isBlank) {
                setBlackMsg("属性 {0} 的值为null", name);
                return true;
            } else {
                setWhiteMsg("属性 {0} 的值为null", name);
                return false;
            }
        }
        if (!(value instanceof CharSequence)) {
            setBlackMsg("属性 {0} 的值不是字符类型", name);
            return false;
        }

        if (isBlank) {
            if ("".equals(value)) {
                setBlackMsg("属性 {0} 的值为非空字符", name);
                return true;
            } else {
                setWhiteMsg("属性 {0} 的值为空字符", name);
                return false;
            }
        } else {
            if (!"".equals(value)) {
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
        return (null == isBlank);
    }
}
