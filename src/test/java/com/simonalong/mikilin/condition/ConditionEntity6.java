package com.simonalong.mikilin.condition;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/30 3:50 PM
 */
@Data
@Accessors(chain = true)
public class ConditionEntity6 {

    @Matcher(condition = "#current == null && #root.f2 == null && #root.f3 == null", accept = false, errMsg = "至少一个为非空")
    private Long f1;
    private String f2;
    private Integer f3;
}
