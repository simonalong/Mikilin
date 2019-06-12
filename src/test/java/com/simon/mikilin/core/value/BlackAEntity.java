package com.simon.mikilin.core.value;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:21
 */
@Data
@Accessors(chain = true)
public class BlackAEntity {
    @FieldBlackMatcher({"a","b","c","null"})
    private String name;
    private Integer age;
}
