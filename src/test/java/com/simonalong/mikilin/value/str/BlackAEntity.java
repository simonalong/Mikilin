package com.simonalong.mikilin.value.str;

import com.simonalong.mikilin.annotation.BlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:21
 */
@Data
@Accessors(chain = true)
public class BlackAEntity {
    @BlackMatcher({"a","b","c","null"})
    private String name;
    private Integer age;
}
