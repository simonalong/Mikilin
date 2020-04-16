package com.simonalong.mikilin.condition;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 下午4:10
 */
@Data
@Accessors(chain = true)
public class ConditionEntity2 {

    @Matcher(condition = "#root.judge")
    private Integer age;

    private Boolean judge;
}
