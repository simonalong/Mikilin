package com.simonalong.mikilin.group;

import com.simonalong.mikilin.MkConstant;
import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.annotation.Matchers;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/3 上午10:19
 */
@Data
@Accessors(chain = true)
public class GroupMultiEntity {

    @Matchers({
        @Matcher(range = "[10, 20)", accept = false),
        @Matcher(group = "test0", range = "[70, 80)", accept = false),
        @Matcher(group = {"test1","test2"}, range = "[20, 30)", accept = false),
        // 下面的会覆盖上面的默认组
        @Matcher(group = {MkConstant.DEFAULT_GROUP,"test3"}, range = "[30, 40)", accept = false),
    })
    private Integer age;

    @Matchers({
        @Matcher(value = {"hangzhou", "guangzhou"}),
        @Matcher(group = {"test2", "test3"}, value = {"beijing", "shanghai"}),
    })
    private String name;
}
