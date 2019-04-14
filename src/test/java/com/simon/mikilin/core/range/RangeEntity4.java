package com.simon.mikilin.core.range;

import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/14 下午12:12
 */
@Data
@Accessors(chain = true)
public class RangeEntity4 {

    /**
     * 属性为大于等于100
     */
    @FieldValidCheck(range = "(100, null)")
    private Integer num1;

    @FieldValidCheck(range = "[100, null)")
    private Integer num2;

    @FieldValidCheck(range = "(null, 50)")
    private Integer num3;

    @FieldValidCheck(range = "(null, 50]")
    private Integer num4;
}
