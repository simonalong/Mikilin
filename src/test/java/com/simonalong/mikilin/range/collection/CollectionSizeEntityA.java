package com.simonalong.mikilin.range.collection;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019-09-15 21:24
 */
@Data
@Accessors(chain = true)
public class CollectionSizeEntityA {

    private String name;

    @Check
    @FieldWhiteMatcher(range = "(0, 2]")
    private List<CollectionSizeEntityB> bList;
}
