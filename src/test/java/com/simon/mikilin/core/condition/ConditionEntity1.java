package com.simon.mikilin.core.condition;

import com.simon.mikilin.core.annotation.FieldWhiteMather;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 下午12:09
 */
@Data
@Accessors(chain = true)
public class ConditionEntity1 {

    @FieldWhiteMather(condition = "#current + #root.num2 > 100")
    private Integer num1;

    @FieldWhiteMather(condition = "#current < 20")
    private Integer num2;

    @FieldWhiteMather(condition = "(++#current) >31")
    private Integer num3;
}
