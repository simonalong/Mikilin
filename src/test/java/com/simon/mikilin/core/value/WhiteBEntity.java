package com.simon.mikilin.core.value;

import com.simon.mikilin.core.annotation.Check;
import com.simon.mikilin.core.annotation.FieldWhiteMatcher;
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
