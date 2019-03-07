package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.FieldCheck;
import com.simon.mikilin.core.annotation.FieldExcludeCheck;
import com.simon.mikilin.core.annotation.FieldIncludeCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:24
 */
@Data
@Accessors(chain = true)
public class BlackWhiteAEntity {

    @FieldIncludeCheck({"a","b"})
    private String name;
    @FieldExcludeCheck({"1","2"})
    private Integer age;
}
