package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.FieldCheck;
import com.simon.mikilin.core.annotation.TypeCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:44
 */
@Data
@TypeCheck
@Accessors(chain = true)
public class WhiteBEntity {

    private BEntity bEntity;
    @FieldCheck(includes = {"a", "b"})
    private String name;
}
