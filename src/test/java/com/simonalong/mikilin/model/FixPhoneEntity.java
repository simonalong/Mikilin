package com.simonalong.mikilin.model;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.FieldModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:50
 */
@Data
@Accessors(chain = true)
public class FixPhoneEntity {

    @Matcher(model = FieldModel.FIXED_PHONE)
    private String fixedPhone;
    @Matcher(model = FieldModel.FIXED_PHONE, accept = false)
    private String fixedPhoneInValid;
}
