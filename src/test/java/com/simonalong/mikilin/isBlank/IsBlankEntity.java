package com.simonalong.mikilin.isBlank;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/18 2:51 PM
 */
@Data
@Accessors(chain = true)
public class IsBlankEntity {

    @Matcher(isBlank = "true")
    private String name;
    private Integer age;
}
