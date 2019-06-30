package com.simonalong.mikilin.range;

import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:42
 */
@Data
@Accessors(chain = true)
public class RangeEntity2 {

    @FieldWhiteMatcher(range = "(0,100]")
    private Integer age3;

    @FieldWhiteMatcher(range = "[0, 100)")
    private Integer age4;

    @FieldWhiteMatcher(range = "(0, 100)")
    private Integer age5;
}
