package com.simonalong.mikilin.enumtype;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
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

    @WhiteMatcher(enumType = AEnum.class)
    private String name;

    @WhiteMatcher(enumType = {AEnum.class, BEnum.class})
    private String tag;

    @BlackMatcher(enumType = {CEnum.class})
    private String invalidTag;
}
