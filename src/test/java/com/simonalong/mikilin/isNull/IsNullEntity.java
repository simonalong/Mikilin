package com.simonalong.mikilin.isNull;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/18 2:35 PM
 */
@Data
@Accessors(chain = true)
public class IsNullEntity {

    @Matcher(isNull = "true", accept = false)
    private String name;

    @Matcher(isNull = "true", accept = false)
    private Integer age;
}
