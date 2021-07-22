package com.simonalong.mikilin.matchChangeto;

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

    @Matcher(range = "[0, 20]", value = "23", matchChangeTo = "30")
    private Integer age2;

    @Matcher(range = "[0, 20]", matchChangeTo = "30", accept = false)
    private Integer age3;

    @Matcher(range = "[0, 20]", matchChangeTo = "30", accept = true)
    private Integer age4;
}
