package com.simonalong.mikilin.model;

import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.match.FieldModel;
import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午3:38
 */
@Data
@Accessors(chain = true)
public class IdCardEntity {

    @FieldWhiteMatcher(model = FieldModel.ID_CARD)
    private String idCardValid;
    @FieldBlackMatcher(model = FieldModel.ID_CARD)
    private String idCardInValid;
}
