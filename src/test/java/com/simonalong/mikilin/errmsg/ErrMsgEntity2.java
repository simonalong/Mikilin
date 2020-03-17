package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/3/18 上午12:00
 */
@Data
@Accessors(chain = true)
public class ErrMsgEntity2 {

    private String name;
    @BlackMatcher(range = "(200, )", errMsg = "年龄为200以上是不合法的")
    private Integer age;
}
