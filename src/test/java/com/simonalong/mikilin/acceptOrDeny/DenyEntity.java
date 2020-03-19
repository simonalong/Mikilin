package com.simonalong.mikilin.acceptOrDeny;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/3/19 下午7:15
 */
@Data
@Accessors(chain = true)
public class DenyEntity {

    @Matcher(value = {"a", "b", "null"}, accept = false)
    private String name;
    @Matcher(range = "[0, 100]", accept = false)
    private Integer age;
}
