package com.simonalong.mikilin.range.number;

import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:02
 */
@Data
@Accessors(chain = true)
public class RangeEntity1 {

    @WhiteMatcher(range = "[0,100]")
    private Integer age1;

    @WhiteMatcher(range = "[0, 100]")
    private Integer age2;
}
