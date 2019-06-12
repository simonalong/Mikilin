package com.simon.mikilin.core.range;

import com.simon.mikilin.core.annotation.FieldWhiteMather;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:42
 */
@Data
@Accessors(chain = true)
public class RangeEntity2 {

    @FieldWhiteMather(range = "(0,100]")
    private Integer age3;

    @FieldWhiteMather(range = "[0, 100)")
    private Integer age4;

    @FieldWhiteMather(range = "(0, 100)")
    private Integer age5;
}
