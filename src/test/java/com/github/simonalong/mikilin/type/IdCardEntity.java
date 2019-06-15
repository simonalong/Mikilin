package com.github.simonalong.mikilin.type;

import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.github.simonalong.mikilin.match.FieldType;
import com.github.simonalong.mikilin.annotation.FieldBlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午3:38
 */
@Data
@Accessors(chain = true)
public class IdCardEntity {

    @FieldWhiteMatcher(type = FieldType.ID_CARD)
    private String idCardValid;
    @FieldBlackMatcher(type = FieldType.ID_CARD)
    private String idCardInValid;
}
