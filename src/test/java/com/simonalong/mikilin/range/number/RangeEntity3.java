package com.simonalong.mikilin.range.number;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:44
 */
@Data
@Accessors(chain = true)
public class RangeEntity3 {

    @WhiteMatcher(range = "[0.00,3.00]")
    private Float height;

    @BlackMatcher(range = "[10,10000]")
    private Double money;

}
