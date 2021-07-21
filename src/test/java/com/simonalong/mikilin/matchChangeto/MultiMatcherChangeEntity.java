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
public class MultiMatcherChangeEntity {

    @Matcher(range = "[0, 10]", matchChangeTo = "100")
    @Matcher(condition = "null != #current && (#current % 2 == 0)")
    private Integer age;

    @Matcher(range = "[10, 20]")
    @Matcher(condition = "null != #current && (#current % 2 == 0)", matchChangeTo = "200")
    private Integer age1;
//
//    @Matcher(range = "[20, 30]")
//    @Matcher(condition = "#current % 2 == 0", matchChangeTo = "300")
//    private Integer age2;
}
