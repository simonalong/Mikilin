package com.github.simonalong.mikilin.type;

import com.github.simonalong.mikilin.match.FieldType;
import com.github.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:51
 */
@Data
@Accessors(chain = true)
public class MailEntity {

    @FieldWhiteMatcher(type = FieldType.MAIL)
    private String mailValid;
    @FieldBlackMatcher(type = FieldType.MAIL)
    private String mailInValid;
}
