package com.simonalong.mikilin.model;

import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.match.FieldModel;
import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:50
 */
@Data
@Accessors(chain = true)
public class FixPhoneEntity {

    @FieldWhiteMatcher(model = FieldModel.FIXED_PHONE)
    private String fixedPhone;
    @FieldBlackMatcher(model = FieldModel.FIXED_PHONE)
    private String fixedPhoneInValid;
}
