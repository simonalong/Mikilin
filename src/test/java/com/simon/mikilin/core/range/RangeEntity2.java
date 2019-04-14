package com.simon.mikilin.core.range;

import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:42
 */
@Data
@Accessors(chain = true)
public class RangeEntity2 {

    @FieldValidCheck(range = "(0,100]")
    private Integer age3;

    @FieldValidCheck(range = "[0, 100)")
    private Integer age4;

    @FieldValidCheck(range = "(0, 100)")
    private Integer age5;
}
