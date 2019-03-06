package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.FieldCheck;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午7:05
 */
@Data
@Accessors(chain = true)
public class
CEntity {

    @FieldCheck(includes = {"a", "b"})
    private String name;
    @FieldCheck
    private List<BEntity> bEntities;
}
