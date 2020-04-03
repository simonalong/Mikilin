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
public class EnumTypeEntity2 {

    @Matcher(enumType = {AEnum.class, CEnum.class})
    private Integer name;
}
