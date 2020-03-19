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
public class AcceptEntity {

    @Matcher(value = {"a", "b", "null"})
    private String name;
    @Matcher(range = "[0, 100]")
    private Integer age;
}
