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

    // 转换匹配，302生效，即最下面的生效
    @Matcher(range = "[20, 30]", matchChangeTo = "301")
    @Matcher(condition = "null != #current && (#current % 2 == 0)", matchChangeTo = "302")
    private Integer age2;

    // 转换匹配，301生效，即最下面的生效
    @Matcher(condition = "null != #current && (#current % 2 == 0)", matchChangeTo = "302")
    @Matcher(range = "[20, 30]", matchChangeTo = "301")
    private Integer age3;
}
