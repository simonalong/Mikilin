package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.FieldCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:18
 */
@Data
@Accessors(chain = true)
public class WhiteAEntity {
    @FieldCheck(includes = {"a","b","c","null"})
    private String name;
    private String address;
}
