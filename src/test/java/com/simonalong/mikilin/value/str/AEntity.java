package com.simonalong.mikilin.value.str;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午10:58
 */
@Data
@Accessors(chain = true)
public class AEntity {
    @Matcher({"a", "b", "c", "null"})
    private String name;
    @Matcher(value = {"null"}, accept = false)
    private Integer age;
    private String address;
}
