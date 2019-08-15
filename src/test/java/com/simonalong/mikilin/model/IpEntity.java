package com.simonalong.mikilin.model;

import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.match.FieldModel;
import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:51
 */
@Data
@Accessors(chain = true)
public class IpEntity {

    @FieldWhiteMatcher(model = FieldModel.IP_ADDRESS)
    private String ipValid;
    @FieldBlackMatcher(model = FieldModel.IP_ADDRESS)
    private String ipInvalid;
}
