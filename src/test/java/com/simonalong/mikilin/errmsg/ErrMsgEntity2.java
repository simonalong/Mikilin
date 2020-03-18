package com.simonalong.mikilin.errmsg;

import com.simonalong.mikilin.annotation.Matcher;
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
    @Matcher(range = "(200, )", errMsg = "年龄为200以上是不合法的", acceptOrDeny = false)
    private Integer age;
}
