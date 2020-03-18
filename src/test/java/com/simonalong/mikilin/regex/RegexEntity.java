package com.simonalong.mikilin.regex;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:13
 */
@Data
@Accessors(chain = true)
public class RegexEntity {

    @Matcher(regex = "^\\d+$")
    private String regexValid;

    @Matcher(regex = "^\\d+$", acceptOrDeny = false)
    private String regexInValid;
}
