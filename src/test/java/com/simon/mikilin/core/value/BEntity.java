package com.simon.mikilin.core.value;

import com.simon.mikilin.core.annotation.Check;
import com.simon.mikilin.core.annotation.FieldWhiteMatcher;
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
