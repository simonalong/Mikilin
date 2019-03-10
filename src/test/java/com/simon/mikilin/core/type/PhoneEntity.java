package com.simon.mikilin.core.type;

import com.simon.mikilin.core.annotation.FieldEnum;
import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:49
 */
@Data
@Accessors(chain = true)
public class PhoneEntity {

    @FieldValidCheck(type = FieldEnum.PHONE_NUM)
    private String phoneValid;
    @FieldInvalidCheck(type = FieldEnum.PHONE_NUM)
    private String phoneInValid;
}
