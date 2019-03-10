package com.simon.mikilin.core.white;

import com.simon.mikilin.core.annotation.Check;
import com.simon.mikilin.core.value.BEntity;
import com.simon.mikilin.core.value.CEntity;
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

    @Check
    private List<CEntity> cEntities;
    @Check
    private BEntity bEntity;
}
