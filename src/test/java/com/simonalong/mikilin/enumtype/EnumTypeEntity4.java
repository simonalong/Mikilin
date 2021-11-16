package com.simonalong.mikilin.enumtype;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/4/2 5:54 PM
 */
@Data
@Accessors(chain = true)
public class EnumTypeEntity4 {

    @Matcher(enumType = DEnum.class)
    private DEnum name;
}
