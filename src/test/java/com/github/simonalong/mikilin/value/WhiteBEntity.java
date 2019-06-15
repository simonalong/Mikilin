package com.github.simonalong.mikilin.value;

import com.github.simonalong.mikilin.annotation.Check;
import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
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
    @FieldWhiteMatcher({"a", "b"})
    private String name;
}
