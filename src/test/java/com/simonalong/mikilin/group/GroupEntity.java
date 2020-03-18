package com.simonalong.mikilin.group;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.annotation.Matchers;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/6/15 上午12:02
 */
@Data
@Accessors(chain = true)
public class GroupEntity {

    @Matchers({
        @Matcher(range = "[50, 100]", acceptOrDeny = false),
        @Matcher(group = "test1", range = "[12, 23]", acceptOrDeny = false),
        @Matcher(group = "test2", range = "[1, 10]", acceptOrDeny = false)
    })
    private Integer age;

    @Matchers({
        @Matcher(value = {"beijing", "shanghai", "guangzhou"}),
        @Matcher(group = "test1", value = {"beijing", "shanghai"}),
        @Matcher(group = "test2", value = {"shanghai", "hangzhou"})
    })
    private String name;
}
