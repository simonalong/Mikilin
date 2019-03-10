package com.simon.mikilin.core.type;

import com.simon.mikilin.core.annotation.FieldEnum;
import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午3:38
 */
@Data
@Accessors(chain = true)
public class IdCardEntity {

    @FieldValidCheck(type = FieldEnum.ID_CARD)
    private String idCardValid;
    @FieldInvalidCheck(type = FieldEnum.ID_CARD)
    private String idCardInValid;
}
