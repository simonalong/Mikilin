package com.simon.mikilin.core.enumtype;

import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:41
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class JudgeEntity {

    @FieldValidCheck(enumType = AEnum.class)
    private String name;

    @FieldValidCheck(enumType = {AEnum.class, BEnum.class})
    private String tag;

    @FieldInvalidCheck(enumType = {CEnum.class})
    private String invalidTag;
}
