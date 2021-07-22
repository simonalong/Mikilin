package com.simonalong.mikilin.throwable;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2021-07-13 21:46:57
 */
@Data
@Accessors(chain = true)
public class ParameterFunOfThrowableService {

    public String funValue(@Matcher(value = {"zhou", "song"}, throwable = TestException.class) String name) {
        return name;
    }
}
