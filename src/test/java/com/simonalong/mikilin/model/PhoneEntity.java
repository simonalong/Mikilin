package com.simonalong.mikilin.model;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.FieldModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:49
 */
@Data
@Accessors(chain = true)
public class PhoneEntity {

    @Matcher(model = FieldModel.PHONE_NUM)
    private String phoneValid;
    @Matcher(model = FieldModel.PHONE_NUM, acceptOrDeny = false)
    private String phoneInValid;
}
