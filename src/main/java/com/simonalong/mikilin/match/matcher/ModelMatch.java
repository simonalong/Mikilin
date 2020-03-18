package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.Builder;
import com.simonalong.mikilin.match.FieldModel;

/**
 * 指定的类型判断，对应{@link Matcher#model()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:50
 */
public class ModelMatch extends AbstractBlackWhiteMatch implements Builder<ModelMatch, FieldModel> {

    private FieldModel fieldModel;

    @Override
    public boolean match(Object object, String nam, Object value) {
        if (value instanceof String) {
            if (fieldModel.valid((String) value)) {
                setBlackMsg("属性 {0} 的值 {1} 命中不允许的类型 [FieldModel-{2}]", nam, value, fieldModel.getName());
                return true;
            } else {
                setWhiteMsg("属性 {0} 的值 {1} 没有命中只允许类型 [FieldModel-{2}]", nam, value, fieldModel.name());
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return null == fieldModel;
    }

    @Override
    public ModelMatch build(FieldModel obj) {
        if (obj.equals(FieldModel.DEFAULT)) {
            return null;
        }
        this.fieldModel = obj;
        return this;
    }
}
