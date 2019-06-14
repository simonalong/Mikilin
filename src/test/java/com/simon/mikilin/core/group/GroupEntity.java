package com.simon.mikilin.core.group;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldBlackMatchers;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/6/15 上午12:02
 */
@Data
@Accessors(chain = true)
public class GroupEntity {

    @FieldBlackMatchers({
        @FieldBlackMatcher(group = "test1", range = "[12, 23]"),
        @FieldBlackMatcher(group = "test2", range = "[1, 10]")
    })
    private Integer age;

    private Boolean judge;
}
