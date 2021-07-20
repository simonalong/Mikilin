package com.simonalong.mikilin.notBlank;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/18 4:53 PM
 */
@Data
@Accessors(chain = true)
public class NotBlankEntity2 {

    @Matcher(notBlank = "true")
    private String name;
    /**
     * 有异常
     */
    @Matcher(notBlank = "true")
    private Integer age;
}
