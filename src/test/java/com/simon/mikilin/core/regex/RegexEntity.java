package com.simon.mikilin.core.regex;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:13
 */
@Data
@Accessors(chain = true)
public class RegexEntity {

    @FieldWhiteMatcher(regex = "^\\d+$")
    private String regexValid;

    @FieldBlackMatcher(regex = "^\\d+$")
    private String regexInValid;
}
