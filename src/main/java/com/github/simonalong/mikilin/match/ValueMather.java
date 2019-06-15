package com.github.simonalong.mikilin.match;

import com.github.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.github.simonalong.mikilin.util.Objects;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Setter;

/**
 * 指定的值判断，对应{@link FieldWhiteMatcher#value()}或者{@link FieldBlackMatcher#value()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:49
 */
@Setter
public class ValueMather extends AbstractBlackWhiteMatcher {

    private Set<Object> values;

    @Override
    public boolean match(Object object, String fieldName, Object value) {
        if (values.contains(value)) {
            setBlackMsg("属性[{0}]的值[{1}]位于黑名单[{2}]中", fieldName, value, values.toString());
            return true;
        } else {
            setWhiteMsg("属性[{0}]的值[{1}]不在白单中[{2}]中", fieldName, value, values.toString());
            return false;
        }
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * 将设置的数据转换为对应结构类型的数据
     *
     * @param field 对象的属性类型
     * @param values 属性的可用的或者不可用列表String形式
     */
    static ValueMather build(Field field, String[] values){
        if(null == values || 0 == values.length){
            return null;
        }

        ValueMather valueMather = new ValueMather();
        valueMather.setValues(Arrays.stream(values).map(i -> {
            if (null != i && !"".equals(i)) {
                return Objects.cast(field.getType(), i);
            } else {
                return null;
            }
        }).collect(Collectors.toSet()));

        return valueMather;
    }
}
