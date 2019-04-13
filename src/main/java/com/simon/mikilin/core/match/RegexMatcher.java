package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import java.util.regex.Pattern;

/**
 * 正则表达式判断，对应{@link FieldValidCheck#regex()}或者{@link FieldInvalidCheck#regex()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:50
 */
public class RegexMatcher extends AbstractBlackWhiteMatcher implements Builder<RegexMatcher, String>{

    private Pattern pattern;

    @Override
    public boolean match(String name, Object object) {
        if (object instanceof String) {
            if (pattern.matcher(String.class.cast(object)).matches()) {
                setBlackMsg("属性[{0}]的值[{1}]命中正则表达式黑名单[{2}]", name, object, pattern.pattern());
                return true;
            } else {
                setWhiteMsg("属性[{0}]的值[{1}]没命中正则表达式白名单[{2}]", name, object, pattern.pattern());
            }
        }else{
            setWhiteMsg("属性[{0}]的值[{1}]不是String类型", name, object);
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
