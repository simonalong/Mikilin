package com.simonalong.mikilin.typeClass;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/3/24 下午2:12
 */
@Data
@Accessors(chain = true)
public class DataEntity {

    @Matcher(value = {"a", "b"})
    private String name;
}
