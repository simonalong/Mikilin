package com.simon.mikilin.core.type;

import com.simon.mikilin.core.annotation.FieldWhiteMatcher;
import com.simon.mikilin.core.match.FieldType;
import com.simon.mikilin.core.annotation.FieldBlackMatcher;
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
