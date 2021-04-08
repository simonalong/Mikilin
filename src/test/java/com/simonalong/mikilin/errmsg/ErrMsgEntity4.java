package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020-12-27 21:21:25
 */
@Data
@Accessors(chain = true)
public class ErrMsgEntity4 {

    private Integer age2;

    private Integer age;

    @Matcher(value = {"a", "b", "c"}, errMsg = "值#current不符合要求, 值age=#root.age，值age2=#root.age2")
    private String name;
}
