package com.simonalong.mikilin.group;

import com.simonalong.mikilin.MkConstant;
import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.BlackMatchers;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import com.simonalong.mikilin.annotation.WhiteMatchers;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/3 上午10:19
 */
@Data
@Accessors(chain = true)
public class GroupMultiEntity {

    @BlackMatchers({
        @BlackMatcher(range = "[10, 20)"),
        @BlackMatcher(group = "test0", range = "[70, 80)"),
        @BlackMatcher(group = {"test1","test2"}, range = "[20, 30)"),
        // 下面的会覆盖上面的默认组
        @BlackMatcher(group = {MkConstant.DEFAULT_GROUP,"test3"}, range = "[30, 40)"),
    })
    private Integer age;

    @WhiteMatchers({
        @WhiteMatcher(value = {"hangzhou", "guangzhou"}),
        @WhiteMatcher(group = {"test2", "test3"}, value = {"beijing", "shanghai"}),
    })
    private String name;
}
