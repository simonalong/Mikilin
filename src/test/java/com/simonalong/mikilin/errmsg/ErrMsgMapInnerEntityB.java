package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2021-06-02 19:47:20
 */
@Data
@Accessors(chain = true)
public class ErrMsgMapInnerEntityB {

    @Check
    private ErrMsgMapInnerEntityA innerEntityA;
}
