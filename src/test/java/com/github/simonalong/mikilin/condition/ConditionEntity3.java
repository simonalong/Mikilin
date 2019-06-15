package com.github.simonalong.mikilin.condition;

import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 下午4:12
 */
@Data
@Accessors(chain = true)
public class ConditionEntity3 {

    @FieldWhiteMatcher(condition = "min(#current, #root.num2) > #root.num3")
    private Integer num1;
    private Integer num2;
    private Integer num3;
}
