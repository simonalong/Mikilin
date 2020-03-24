package com.simonalong.mikilin.muti;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.FieldModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/3/24 下午6:24
 */
@Data
@Accessors(chain = true)
public class MultiMatcherEntity {

    @Matcher(value = {"a", "b"}, model = FieldModel.ID_CARD)
    private String name;
    @Matcher(range = "[0, 100]", value = {"222", "111"})
    private Integer code;
}
