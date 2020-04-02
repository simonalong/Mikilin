package com.simonalong.mikilin.match.matcher;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.util.ObjectUtil;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Setter;

/**
 * 指定的值判断，对应{@link Matcher#value()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:49
 */
@Setter
public class ValueMath extends AbstractBlackWhiteMatch {

    private Set<Object> values;

    @Override
    public boolean match(Object object, String fieldName, Object value) {
        if (values.contains(value)) {
            setBlackMsg("属性 {0} 的值 {1} 位于禁用值 {2}中", fieldName, value, values.toString());
            return true;
        } else {
            setWhiteMsg("属性 {0} 的值 {1} 不在只可用列表 {2} 中", fieldName, value, values.toString());
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
     * @return 值匹配器
     */
    public static ValueMath build(Field field, String[] values) {
        if (null == values || 0 == values.length) {
            return null;
        }

        ValueMath valueMath = new ValueMath();
        valueMath.setValues(Arrays.stream(values).map(i -> {
            if (null != i) {
                return ObjectUtil.cast(field.getType(), i);
            } else {
                return null;
            }
        }).collect(Collectors.toSet()));

        return valueMath;
    }
}
