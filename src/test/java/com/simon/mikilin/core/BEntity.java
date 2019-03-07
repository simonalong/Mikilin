package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.Check;
import com.simon.mikilin.core.annotation.FieldCheck;
import com.simon.mikilin.core.annotation.FieldIncludeCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午10:58
 */
@Data
@Accessors(chain = true)
public class BEntity {

    @FieldIncludeCheck({"a","b"})
    private String name;
    @Check
    private AEntity aEntity;
}
