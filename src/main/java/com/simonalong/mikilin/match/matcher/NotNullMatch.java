package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.Builder;

/**
 * 拦截null数据，对应{@link Matcher#notNull()}
 *
 * @author shizi
 * @since 2020/6/18 11:52 AM
 */
public class NotNullMatch extends AbstractBlackWhiteMatch implements Builder<NotNullMatch, String> {

    private Boolean notNull;

    @Override
    public NotNullMatch build(String obj) {
        if (!"".equals(obj)) {
            notNull = Boolean.parseBoolean(obj);
        }
        return this;
    }

    @Override
    public boolean match(Object object, String name, Object value) {
        if (notNull) {
            if(null != value) {
                setBlackMsg("属性 {0} 的值为null", name);
                return true;
            } else{
                setWhiteMsg("属性 {0} 的值为null", name);
                return false;
            }
        } else {
            if(null == value) {
                setBlackMsg("属性 {0} 的值不为null", name);
                return true;
            } else{
                setWhiteMsg("属性 {0} 的值非空", name);
                return false;
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return (null == notNull);
    }
}
