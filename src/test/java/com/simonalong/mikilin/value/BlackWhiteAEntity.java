package com.simonalong.mikilin.value;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:24
 */
@Data
@Accessors(chain = true)
public class BlackWhiteAEntity {

    @WhiteMatcher({"a","b"})
    private String name;
    @BlackMatcher({"1","2"})
    private Integer age;
}
