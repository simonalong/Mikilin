package com.simonalong.mikilin.common;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/6/12 下午10:04
 */
@Data
@Accessors(chain = true)
public class TestEntity {

    @BlackMatcher({"nihao", "ok"})
    private String name;
    @WhiteMatcher(range = "[12, 32]")
    private Integer age;
    @WhiteMatcher({"beijing", "shanghai"})
    private String address;
}
