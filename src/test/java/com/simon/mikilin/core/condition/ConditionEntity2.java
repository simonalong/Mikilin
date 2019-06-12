package com.simon.mikilin.core.condition;

import com.simon.mikilin.core.annotation.FieldWhiteMather;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 下午4:10
 */
@Data
@Accessors(chain = true)
public class ConditionEntity2 {

    @FieldWhiteMather(condition = "#root.judge")
    private Integer age;

    private Boolean judge;
}
