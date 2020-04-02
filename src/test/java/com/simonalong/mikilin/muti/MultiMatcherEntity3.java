package com.simonalong.mikilin.muti;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/4/2 5:01 PM
 */
@Data
@Accessors(chain = true)
public class MultiMatcherEntity3 {


    /**
     * 数据：为偶数，而且是在0~100这个范围
     */
    @Matcher(group = "偶数", condition = "#current %2 ==0", errMsg = "值#current不是偶数")
    @Matcher(group = "偶数", range = "[0, 100]", errMsg = "值#current没有在0~100范围中")
    @Matcher(group = "奇数", condition = "#current %2 ==1", errMsg = "值#current不是奇数")
    @Matcher(group = "奇数", range = "[100, 200]", errMsg = "值#current没有在100~200范围中")
    private Integer code;
}
