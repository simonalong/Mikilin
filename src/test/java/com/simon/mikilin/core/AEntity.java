package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.FieldCheck;
import com.simon.mikilin.core.annotation.FieldExcludeCheck;
import com.simon.mikilin.core.annotation.FieldIncludeCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午10:58
 */
@Data
@Accessors(chain = true)
public class AEntity {
    @FieldIncludeCheck({"a", "b", "c", "null"})
    private String name;
    @FieldExcludeCheck({"null"})
    private Integer age;
    private String address;
}
