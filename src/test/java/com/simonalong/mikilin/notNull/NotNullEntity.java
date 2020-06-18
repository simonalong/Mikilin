package com.simonalong.mikilin.notNull;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/18 2:35 PM
 */
@Data
@Accessors(chain = true)
public class NotNullEntity {

    @Matcher(notNull = "true")
    private String name;

    @Matcher(notNull = "true")
    private Integer age;
}
