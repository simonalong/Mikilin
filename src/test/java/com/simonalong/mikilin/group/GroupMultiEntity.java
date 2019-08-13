package com.simonalong.mikilin.group;

import com.simonalong.mikilin.MkConstant;
import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldBlackMatchers;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatchers;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/3 上午10:19
 */
@Data
@Accessors(chain = true)
public class GroupMultiEntity {

    @FieldBlackMatchers({
        @FieldBlackMatcher(range = "[10, 20)"),
        @FieldBlackMatcher(group = "test0", range = "[70, 80)"),
        @FieldBlackMatcher(group = {"test1","test2"}, range = "[20, 30)"),
        // 下面的会覆盖上面的默认组
        @FieldBlackMatcher(group = {MkConstant.DEFAULT_GROUP,"test3"}, range = "[30, 40)"),
    })
    private Integer age;

    @FieldWhiteMatchers({
        @FieldWhiteMatcher(value = {"hangzhou", "guangzhou"}),
        @FieldWhiteMatcher(group = {"test2", "test3"}, value = {"beijing", "shanghai"}),
    })
    private String name;
}
