package com.github.simonalong.mikilin.value;

import com.github.simonalong.mikilin.annotation.Check;
import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午10:58
 */
@Data
@Accessors(chain = true)
public class BEntity {

    @FieldWhiteMatcher({"a","b"})
    private String name;
    @Check
    private AEntity aEntity;
}
