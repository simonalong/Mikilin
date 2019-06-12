package com.simon.mikilin.core.range;

import com.simon.mikilin.core.annotation.FieldWhiteMather;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:02
 */
@Data
@Accessors(chain = true)
public class RangeEntity1 {

    @FieldWhiteMather(range = "[0,100]")
    private Integer age1;

    @FieldWhiteMather(range = "[0, 100]")
    private Integer age2;
}
