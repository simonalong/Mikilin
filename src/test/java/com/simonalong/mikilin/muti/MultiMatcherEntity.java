package com.simonalong.mikilin.muti;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/3/24 下午6:24
 */
@Data
@Accessors(chain = true)
public class MultiMatcherEntity {

    /**
     * 市编码
     */
    @Matcher(value = "12")
    private String cityCode;
    /**
     * 名称
     */
    @Matcher(value = "11", range = "[0, 10]")
    private Integer age;

    /**
     * 名称
     */
    @Matcher(value = "33", range = "[10, 20]", accept = false)
    private Integer code;
}
