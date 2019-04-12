package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import com.simon.mikilin.core.util.Objects;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Setter;

/**
 * 指定的值判断，对应{@link FieldValidCheck#value()}或者{@link FieldInvalidCheck#value()}
 *
 * @author zhouzhenyong
 * @since 2019/4/11 下午8:49
 */
@Setter
public class ValueMather implements Matcher{

    private Set<Object> values;
    private String errMsg;

    @Override
    public boolean match(String fieldName, Object object) {
        if (values.contains(object)){
            return true;
        }else{
            errMsg = MessageFormat.format("属性[{0}]的值[{1}]位于名单中{2}", fieldName, object, values.toArray());
            return false;
        }
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public String errMsg() {
        return errMsg;
    }

    /**
     * 将设置的数据转换为对应结构类型的数据
     *
     * @param field 对象的属性类型
     * @param values 属性的可用的或者不可用列表String形式
     */
    ValueMather(Field field, String[] values){
        this.values = Arrays.stream(values).map(i -> {
            if (null != i && !"".equals(i)) {
                return Objects.cast(field.getType(), i);
            } else {
                return null;
            }
        }).collect(Collectors.toSet());
    }
}
