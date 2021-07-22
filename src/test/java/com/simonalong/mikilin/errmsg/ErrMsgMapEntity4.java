package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author shizi
 * @since 2021-06-02 19:46:09
 */
@Data
@Accessors(chain = true)
public class ErrMsgMapEntity4 {

    @Matcher(value = {"a", "b"})
    private String name;
    @Check
    private List<ErrMsgMapInnerEntityB> innerEntityBList;
}
