package com.simonalong.mikilin.muti;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/4/2 1:54 PM
 */
@Data
@Accessors(chain = true)
public class MultiMatcherEntity2 {

    /**
     * 数据：为偶数，而且是在0~100这个范围
     */
    @Matcher(condition = "#current %2 ==0", errMsg = "值#current不是偶数")
    @Matcher(range = "[0, 100]", errMsg = "值#current没有在0~100范围中")
    private Integer code;
}
