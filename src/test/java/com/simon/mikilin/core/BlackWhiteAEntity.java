package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.FieldCheck;
import com.simon.mikilin.core.annotation.TypeCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:24
 */
@Data
@TypeCheck
@Accessors(chain = true)
public class BlackWhiteAEntity {

    @FieldCheck(includes = {"a","b"})
    private String name;
    @FieldCheck(excludes = {"1","2"})
    private Integer age;
}
