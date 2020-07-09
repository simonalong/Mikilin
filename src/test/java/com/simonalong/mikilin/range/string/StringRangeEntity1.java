package com.simonalong.mikilin.range.string;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/7/9 7:57 PM
 */
@Data
@Accessors(chain = true)
public class StringRangeEntity1 {

    @Matcher(range = "[, 3]")
    private String data;
}
