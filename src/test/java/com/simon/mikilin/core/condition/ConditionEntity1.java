package com.simon.mikilin.core.condition;

import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 下午12:09
 */
@Data
public class ConditionEntity1 {

    @FieldValidCheck(condition = "#current + #root.num2 > 100")
    private Integer num1;

    private Integer num2;
}
