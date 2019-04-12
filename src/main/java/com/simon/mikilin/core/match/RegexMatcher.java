package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * 正则表达式判断，对应{@link FieldValidCheck#regex()}或者{@link FieldInvalidCheck#regex()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:50
 */
public class RegexMatcher implements Matcher, Builder<RegexMatcher, String>{

    private Pattern pattern;
    private String errMsg;

    @Override
    public boolean match(String name, Object object) {
        if (object instanceof String) {
            if (pattern.matcher(String.class.cast(object)).matches()) {
                return true;
            } else {
                errMsg = MessageFormat.format("属性[{0}]的值[{1}]命中正则表达式[{2}]", name, object, pattern.pattern());
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return (null == pattern);
    }

    @Override
    public String errMsg() {
        return errMsg;
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
