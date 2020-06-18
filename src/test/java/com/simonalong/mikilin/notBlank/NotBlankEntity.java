package com.simonalong.mikilin.notBlank;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/18 2:51 PM
 */
@Data
@Accessors(chain = true)
public class NotBlankEntity {

    @Matcher(notBlank = "true")
    private String name;
    private Integer age;
}
