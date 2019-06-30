package com.simonalong.mikilin.match;

import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import java.util.regex.Pattern;

/**
 * 正则表达式判断，对应{@link FieldWhiteMatcher#regex()}或者{@link FieldBlackMatcher#regex()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:50
 */
public class RegexMatcher extends AbstractBlackWhiteMatcher implements Builder<RegexMatcher, String>{

    private Pattern pattern;

    @Override
    public boolean match(Object object, String name, Object value) {
        if (value instanceof String) {
            if (pattern.matcher(String.class.cast(value)).matches()) {
                setBlackMsg("属性[{0}]的值[{1}]命中正则表达式黑名单[{2}]", name, value, pattern.pattern());
                return true;
            } else {
                setWhiteMsg("属性[{0}]的值[{1}]没命中正则表达式白名单[{2}]", name, value, pattern.pattern());
            }
        }else{
            setWhiteMsg("属性[{0}]的值[{1}]不是String类型", name, value);
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return (null == pattern);
    }

    @Override
    public RegexMatcher build(String obj) {
        if (null == obj || "".equals(obj)){
            return null;
        }
        this.pattern = Pattern.compile(obj);
        return this;
    }
}
