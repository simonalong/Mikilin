package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2021-06-02 19:38:21
 */
@Data
@Accessors(chain = true)
public class ErrMsgMapEntity1 {

    private Integer age2;

    private Integer age;

    @Matcher(value = {"a", "b", "c"}, errMsg = "值#current不符合要求, 值age=#root.age，值age2=#root.age2")
    private String name;
}
