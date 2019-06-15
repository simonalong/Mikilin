package com.github.simonalong.mikilin.value;

import com.github.simonalong.mikilin.annotation.Check;
import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
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

    @FieldWhiteMatcher({"a", "b"})
    private String name;
    @Check
    private List<BEntity> bEntities;
}
