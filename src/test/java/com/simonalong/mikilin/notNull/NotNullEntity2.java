package com.simonalong.mikilin.notNull;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/18 5:11 PM
 */
@Data
@Accessors(chain = true)
public class NotNullEntity2 {

    @Matcher(notNull = "false")
    private String name;

    @Matcher(notNull = "false")
    private Integer age;
}
