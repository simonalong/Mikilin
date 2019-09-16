package com.simonalong.mikilin.regex;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:13
 */
@Data
@Accessors(chain = true)
public class RegexEntity {

    @WhiteMatcher(regex = "^\\d+$")
    private String regexValid;

    @BlackMatcher(regex = "^\\d+$")
    private String regexInValid;
}
