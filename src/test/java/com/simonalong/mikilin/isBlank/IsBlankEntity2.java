package com.simonalong.mikilin.isBlank;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/6/18 4:53 PM
 */
@Data
@Accessors(chain = true)
public class IsBlankEntity2 {

    /**
     * 有异常
     */
    @Matcher(isBlank = "true")
    private Integer age;
}
