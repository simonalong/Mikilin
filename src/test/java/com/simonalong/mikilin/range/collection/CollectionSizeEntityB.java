package com.simonalong.mikilin.range.collection;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019-09-15 21:25
 */
@Data
@Accessors(chain = true)
public class CollectionSizeEntityB {

    @Matcher(range = "(10, 30)")
    private Integer bField;
}
