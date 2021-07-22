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
public class ErrMsgMapEntity5 {

    @Matcher(value = {"30", "40", "50"}, range = "[0, 12]", errMsg = "值#current不符合要求")
    private Integer age;

    @Matcher(value = {"30", "40", "50"}, range = "[0, 12]")
    private Integer age2;

    private String name;
}
