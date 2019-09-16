package com.simonalong.mikilin.range.number;

import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:42
 */
@Data
@Accessors(chain = true)
public class RangeEntity2 {

    @WhiteMatcher(range = "(0,100]")
    private Integer age3;

    @WhiteMatcher(range = "[0, 100)")
    private Integer age4;

    @WhiteMatcher(range = "(0, 100)")
    private Integer age5;
}
