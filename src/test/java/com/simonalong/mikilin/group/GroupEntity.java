package com.simonalong.mikilin.group;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.BlackMatchers;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import com.simonalong.mikilin.annotation.WhiteMatchers;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/6/15 上午12:02
 */
@Data
@Accessors(chain = true)
public class GroupEntity {

    @BlackMatchers({
        @BlackMatcher(range = "[50, 100]"),
        @BlackMatcher(group = "test1", range = "[12, 23]"),
        @BlackMatcher(group = "test2", range = "[1, 10]")
    })
    private Integer age;

    @WhiteMatchers({
        @WhiteMatcher(value = {"beijing", "shanghai", "guangzhou"}),
        @WhiteMatcher(group = "test1", value = {"beijing", "shanghai"}),
        @WhiteMatcher(group = "test2", value = {"shanghai", "hangzhou"})
    })
    private String name;
}
