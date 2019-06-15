package com.github.simonalong.mikilin.enumtype;

import com.github.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
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

    @FieldWhiteMatcher(enumType = AEnum.class)
    private String name;

    @FieldWhiteMatcher(enumType = {AEnum.class, BEnum.class})
    private String tag;

    @FieldBlackMatcher(enumType = {CEnum.class})
    private String invalidTag;
}
