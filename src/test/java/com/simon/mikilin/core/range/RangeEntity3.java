package com.simon.mikilin.core.range;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:44
 */
@Data
@Accessors(chain = true)
public class RangeEntity3 {

    @FieldValidCheck(range = "[0.00,3.00]")
    private Float height;

    @FieldInvalidCheck(range = "[10,10000]")
    private Double money;

}
