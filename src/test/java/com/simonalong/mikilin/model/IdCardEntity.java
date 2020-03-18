package com.simonalong.mikilin.model;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.FieldModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午3:38
 */
@Data
@Accessors(chain = true)
public class IdCardEntity {

    @Matcher(model = FieldModel.ID_CARD)
    private String idCardValid;
    @Matcher(model = FieldModel.ID_CARD, acceptOrDeny = false)
    private String idCardInValid;
}
