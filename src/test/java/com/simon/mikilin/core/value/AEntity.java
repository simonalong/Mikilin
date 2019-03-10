package com.simon.mikilin.core.value;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午10:58
 */
@Data
@Accessors(chain = true)
public class AEntity {
    @FieldValidCheck({"a", "b", "c", "null"})
    private String name;
    @FieldInvalidCheck({"null"})
    private Integer age;
    private String address;
}
