package com.simonalong.mikilin.value.str;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import com.simonalong.mikilin.value.str.BEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:44
 */
@Data
@Accessors(chain = true)
public class WhiteBEntity {

    @Check
    private BEntity bEntity;
    @WhiteMatcher({"a", "b"})
    private String name;
}
