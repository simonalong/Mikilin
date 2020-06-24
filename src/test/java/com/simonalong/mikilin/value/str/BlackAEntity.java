package com.simonalong.mikilin.value.str;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:21
 */
@Data
@Accessors(chain = true)
public class BlackAEntity {

    @Matcher(value = {"a","b","c","null"}, accept = false)
    private String name;
    private Integer age;
}
