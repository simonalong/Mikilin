package com.simonalong.mikilin.parameter;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2021-07-13 21:46:57
 */
@Data
@Accessors(chain = true)
public class ParameterFunOfChangeToService {

    public String funValue(
        @Matcher(value = {"zhou", "song"}, matchChangeTo = "_default_") String name,
        @Matcher(value = {"1", "2"}, matchChangeTo = "100") Integer age
    ) {
        return name + ":" + age;
    }

    public String funValueBlack(
        @Matcher(value = {"zhou", "song"}, matchChangeTo = "_default_", accept = false) String name,
        @Matcher(value = {"1", "2"}, matchChangeTo = "100", accept = false) Integer age
    ) {
        return name + ":" + age;
    }
}
