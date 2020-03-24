package com.simonalong.mikilin.typeClass;

import com.simonalong.mikilin.annotation.Check;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * ParameterizedType 这种类型的测试
 *
 * @author shizi
 * @since 2020/3/24 下午6:35
 */
@Data
@Accessors(chain = true)
public class ParameterizedTypeEntity {

    private String word;

    @Check
    private Map<String, DataEntity> dataEntityMap;
}
