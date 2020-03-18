package com.simonalong.mikilin.range.number;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:44
 */
@Data
@Accessors(chain = true)
public class RangeEntity3 {

    @Matcher(range = "[0.00,3.00]")
    private Float height;

    @Matcher(range = "[10,10000]", acceptOrDeny = false)
    private Double money;

}
