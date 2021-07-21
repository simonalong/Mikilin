package com.simonalong.mikilin.parameter;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.annotation.Matchers;
import com.simonalong.mikilin.match.FieldModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2021-03-04 21:16:15
 */
@Data
@Accessors(chain = true)
public class ParameterFunService {

    public String funValue(
        @Matcher(value = {"zhou", "song"}) String name,
        @Matcher(value = {"1", "2"}) Integer age
    ) {
        return "ok";
    }

    public String funGroupValue(
        @Matcher(group = "g1", value = {"zhou", "song"}) String name,
        @Matcher(group = "g1", value = {"1", "2"}) Integer age
    ) {
        return "ok";
    }

    public String funGroupsValue(
        @Matchers({
            @Matcher(group = "g1", value = {"zhou", "song"}),
            @Matcher(group = "g2", value = {"chen", "huo"})
        })String name,
        @Matcher(group = "g1", value = {"1", "2"}) Integer age
    ) {
        return "ok";
    }

    public String funNotNull(
        @Matcher(isNull = "false") String name,
        @Matcher(isNull = "false") Integer age
    ) {
        return "ok";
    }

    public String funNotBlank(
        @Matcher(isBlank = "false") String name
    ) {
        return "ok";
    }

    public String funModel(
        @Matcher(model = FieldModel.PHONE_NUM) String name
    ) {
        return "ok";
    }

    public String funRange(
        @Matcher(range = "(1, 4]") String name,
        @Matcher(range = "[10, 20]") Integer age
    ) {
        return "ok";
    }

    public String funCondition(
        @Matcher(condition = "#current <= 12") Integer age
    ) {
        return "ok";
    }

    public String funRegex(
        @Matcher(regex = "^\\d+\\.\\d+\\.\\d+$") String version
    ) {
        return "ok";
    }

    public String funCustomize(
        @Matcher(customize = "com.simonalong.mikilin.parameter.ParameterCustomizeService#testAge") Integer age
    ) {
        return "ok";
    }
}
