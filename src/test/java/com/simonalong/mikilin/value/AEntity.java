package com.simonalong.mikilin.value;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午10:58
 */
@Data
@Accessors(chain = true)
public class AEntity {
    @WhiteMatcher({"a", "b", "c", "null"})
    private String name;
    @BlackMatcher({"null"})
    private Integer age;
    private String address;
}
