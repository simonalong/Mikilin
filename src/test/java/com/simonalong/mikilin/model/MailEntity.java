package com.simonalong.mikilin.model;

import com.simonalong.mikilin.match.FieldModel;
import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:51
 */
@Data
@Accessors(chain = true)
public class MailEntity {

    @WhiteMatcher(model = FieldModel.MAIL)
    private String mailValid;
    @BlackMatcher(model = FieldModel.MAIL)
    private String mailInValid;
}
