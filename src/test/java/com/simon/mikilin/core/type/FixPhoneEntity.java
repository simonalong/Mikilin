package com.simon.mikilin.core.type;

import com.simon.mikilin.core.annotation.FieldWhiteMatcher;
import com.simon.mikilin.core.match.FieldType;
import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:50
 */
@Data
@Accessors(chain = true)
public class FixPhoneEntity {

    @FieldWhiteMatcher(type = FieldType.FIXED_PHONE)
    private String fixedPhone;
    @FieldBlackMatcher(type = FieldType.FIXED_PHONE)
    private String fixedPhoneInValid;
}
