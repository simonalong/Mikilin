package com.simonalong.mikilin.typeClass;

import com.simonalong.mikilin.annotation.Check;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author shizi
 * @since 2020/3/24 下午6:47
 */
@Data
@Accessors(chain = true)
public class WildcardTypeEntity {

    private String wildName;

    @Check
    private Map<String, ? extends DataEntity> dataMap;
}
