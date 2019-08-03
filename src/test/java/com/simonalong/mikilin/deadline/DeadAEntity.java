package com.simonalong.mikilin.deadline;

import com.simonalong.mikilin.annotation.Check;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/3 上午10:39
 */
@Data
@Accessors(chain = true)
public class DeadAEntity {

    @Check
    private DeadBEntity deadBEntity;
}
