package com.simonalong.mikilin.value.str;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:24
 */
@Data
@Accessors(chain = true)
public class BlackWhiteAEntity {

    @Matcher({"a","b"})
    private String name;
    @Matcher(value = {"1","2"}, acceptOrDeny = false)
    private Integer age;
}
