package com.simonalong.mikilin.throwable;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:13
 */
@Data
@Accessors(chain = true)
public class ThrowableEntity {

    /**
     * 表示如果为空，则抛出异常{@link TestException}
     */
    @Matcher(isNull = "true", throwable = TestException.class)
    private String name;
}
