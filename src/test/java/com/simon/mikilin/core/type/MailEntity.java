package com.simon.mikilin.core.type;

import com.simon.mikilin.core.annotation.FieldType;
import com.simon.mikilin.core.annotation.FieldInvalidCheck;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:51
 */
@Data
@Accessors(chain = true)
public class MailEntity {

    @FieldValidCheck(type = FieldType.MAIL)
    private String mailValid;
    @FieldInvalidCheck(type = FieldType.MAIL)
    private String mailInValid;
}
