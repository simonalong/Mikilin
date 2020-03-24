package com.simonalong.mikilin.parent;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/3/25 上午2:34
 */
@Data
@Accessors(chain = true)
public class ParentEntity {

    @Matcher(value = {"a", "b"})
    private String name;

    @Matcher(range = "[0, 100]")
    protected Integer age1;

    @Matcher(range = "[0, 100]")
    Integer age2;

    @Matcher(range = "[0, 100]")
    public Integer age3;
}
