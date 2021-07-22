package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2021-06-02 19:46:09
 */
@Data
@Accessors(chain = true)
public class ErrMsgMapEntity2 {

    @Matcher(value = {"a", "b"})
    private String name;
    @Check
    private ErrMsgMapInnerEntityA innerEntityA;
}
