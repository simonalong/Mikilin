package com.simonalong.mikilin.value.str;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2018/12/26 下午10:58
 */
@Data
@Accessors(chain = true)
public class BEntity {

    @WhiteMatcher({"a","b"})
    private String name;
    @Check
    private AEntity aEntity;
}
