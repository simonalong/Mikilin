package com.simonalong.mikilin.value.str;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.value.str.BEntity;
import com.simonalong.mikilin.value.str.CEntity;
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
