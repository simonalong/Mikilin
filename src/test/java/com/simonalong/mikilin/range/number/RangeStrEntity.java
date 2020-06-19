package com.simonalong.mikilin.range.number;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;

/**
 * @author shizi
 * @since 2020/6/19 4:26 PM
 */
@Data
public class RangeStrEntity {

    @Matcher(range = "[0, 5]")
    private String str;

    @Matcher(range = "(0, 5]", errMsg = "当前字符的个数不符合要求，需要最多为5个")
    private String str2;
}
