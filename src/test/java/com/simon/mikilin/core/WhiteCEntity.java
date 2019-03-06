package com.simon.mikilin.core;

import com.simon.mikilin.core.annotation.FieldCheck;
import com.simon.mikilin.core.annotation.TypeCheck;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午7:04
 */
@Data
@Accessors(chain = true)
public class WhiteCEntity {

    @FieldCheck
    private List<CEntity> cEntities;
    @FieldCheck
    private BEntity bEntity;
}
