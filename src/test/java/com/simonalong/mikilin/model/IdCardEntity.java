package com.simonalong.mikilin.model;

import com.simonalong.mikilin.annotation.WhiteMatcher;
import com.simonalong.mikilin.match.FieldModel;
import com.simonalong.mikilin.annotation.BlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午3:38
 */
@Data
@Accessors(chain = true)
public class IdCardEntity {

    @WhiteMatcher(model = FieldModel.ID_CARD)
    private String idCardValid;
    @BlackMatcher(model = FieldModel.ID_CARD)
    private String idCardInValid;
}
