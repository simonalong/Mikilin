package com.simon.mikilin.core.value;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:24
 */
@Data
@Accessors(chain = true)
public class BlackWhiteAEntity {

    @FieldValidCheck({"a","b"})
    private String name;
    @FieldInvalidCheck({"1","2"})
    private Integer age;
}
