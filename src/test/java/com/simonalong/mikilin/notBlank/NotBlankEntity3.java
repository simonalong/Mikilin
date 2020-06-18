package com.simonalong.mikilin.notBlank;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/18 5:10 PM
 */
@Data
@Accessors(chain = true)
public class NotBlankEntity3 {

    @Matcher(notBlank = "false")
    private String name;
    private Integer age;
}
