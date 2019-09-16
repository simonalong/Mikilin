package com.simonalong.mikilin.condition;

import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 下午12:09
 */
@Data
@Accessors(chain = true)
public class ConditionEntity1 {

    @WhiteMatcher(condition = "#current + #root.num2 > 100")
    private Integer num1;

    @WhiteMatcher(condition = "#current < 20")
    private Integer num2;

    @WhiteMatcher(condition = "(++#current) >31")
    private Integer num3;
}
