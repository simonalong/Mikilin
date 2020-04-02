package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/4/2 1:44 PM
 */
@Data
@Accessors(chain = true)
public class ErrMsgEntity3 {

    @Matcher(value = {"a", "b", "c"}, errMsg = "值#current不符合要求")
    private String name;
}
