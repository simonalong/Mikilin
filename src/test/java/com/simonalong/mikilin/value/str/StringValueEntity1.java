package com.simonalong.mikilin.value.str;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/4/2 3:40 PM
 */
@Data
@Accessors(chain = true)
public class StringValueEntity1 {

    /**
     * 只要空字符，则进行拒绝，其他的都不拦截
     */
    @Matcher(value = "", accept = false, errMsg = "值#current是禁止的")
    private String emptyStr;
}
