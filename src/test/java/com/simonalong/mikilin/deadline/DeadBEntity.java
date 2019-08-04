package com.simonalong.mikilin.deadline;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/3 上午10:40
 */
@Data
@Accessors(chain = true)
public class DeadBEntity {

    @FieldWhiteMatcher({"a", "b"})
    private String name;

    @Check
    private DeadAEntity deadAEntity;
}
