package com.simonalong.mikilin.isNull;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/18 5:11 PM
 */
@Data
@Accessors(chain = true)
public class IsNullEntity2 {

    @Matcher(isNull = "false", accept = true)
    private String name;

    @Matcher(isNull = "false", accept = true)
    private Integer age;
}
