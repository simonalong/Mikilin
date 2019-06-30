package com.simonalong.mikilin.type;

import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.match.FieldType;
import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:49
 */
@Data
@Accessors(chain = true)
public class PhoneEntity {

    @FieldWhiteMatcher(type = FieldType.PHONE_NUM)
    private String phoneValid;
    @FieldBlackMatcher(type = FieldType.PHONE_NUM)
    private String phoneInValid;
}
