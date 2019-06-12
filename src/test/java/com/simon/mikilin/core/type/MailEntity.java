package com.simon.mikilin.core.type;

import com.simon.mikilin.core.match.FieldType;
import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMather;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:51
 */
@Data
@Accessors(chain = true)
public class MailEntity {

    @FieldWhiteMather(type = FieldType.MAIL)
    private String mailValid;
    @FieldBlackMatcher(type = FieldType.MAIL)
    private String mailInValid;
}
