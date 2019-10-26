package com.simonalong.mikilin.value.bool;

import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/10/26 下午9:05
 */
@Data
@Accessors(chain = true)
public class BooleanEntity {

    @WhiteMatcher({"true", "null"})
    private Boolean flag;
}
