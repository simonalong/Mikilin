package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2021-06-02 19:47:20
 */
@Data
@Accessors(chain = true)
public class ErrMsgMapInnerEntityA {

    @Matcher(range = "[0, 12]")
    private Integer age;

    @Matcher(range = "[100, 120]", isNull = "true")
    private Integer length;
}
