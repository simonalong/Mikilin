package com.simonalong.mikilin.value;

import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:18
 */
@Data
@Accessors(chain = true)
public class WhiteAEntity {
    @FieldWhiteMatcher({"a","b","c","null"})
    private String name;
    private String address;
}
