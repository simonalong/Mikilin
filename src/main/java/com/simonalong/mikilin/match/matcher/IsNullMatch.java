package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.Builder;

/**
 * 匹配为null的数据，对应{@link Matcher#isNull()}
 *
 * @author shizi
 * @since 2021-07-14 20:56:58
 */
public class IsNullMatch extends AbstractBlackWhiteMatch implements Builder<IsNullMatch, String> {

    private Boolean isNull;

    @Override
    public IsNullMatch build(String obj) {
        if (null != obj && !"".equals(obj)) {
            isNull = Boolean.parseBoolean(obj);
        }
        return this;
    }

    @Override
    public boolean match(Object object, String name, Object value) {
        if (isNull) {
            if(null == value) {
                setBlackMsg("属性 {0} 的值为null", name);
                return true;
            } else{
                setWhiteMsg("属性 {0} 的值为null", name);
                return false;
            }
        } else {
            if(null != value) {
                setBlackMsg("属性 {0} 的值不为null", name);
                return true;
            } else{
                setWhiteMsg("属性 {0} 的值不为null", name);
                return false;
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return (null == isNull);
    }
}
