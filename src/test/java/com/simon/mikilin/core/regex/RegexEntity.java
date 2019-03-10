package com.simon.mikilin.core.regex;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:13
 */
@Data
@Accessors(chain = true)
public class RegexEntity {

    @FieldValidCheck(regex = "^\\d+$")
    private String regexValid;

    @FieldInvalidCheck(regex = "^\\d+$")
    private String regexInValid;
}
