package com.simonalong.mikilin.changeto;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2021-03-31 23:55:22
 */
@Data
@Accessors(chain = true)
public class ChangeEntity {

    @Matcher(range = "[0, 20]", matchChangeTo = "30")
    private Integer age;

    @Matcher(value = {"", "null"}, matchChangeTo = "_default_")
    private String name;
}
