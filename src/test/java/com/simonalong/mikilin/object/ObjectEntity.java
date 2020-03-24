package com.simonalong.mikilin.object;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/3/25 上午12:29
 */
@Data
@Accessors(chain = true)
public class ObjectEntity {

    @Matcher(value = {"a", "b"})
    private String name;
    private Integer age;
}
