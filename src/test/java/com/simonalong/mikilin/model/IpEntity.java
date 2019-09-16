package com.simonalong.mikilin.model;

import com.simonalong.mikilin.annotation.WhiteMatcher;
import com.simonalong.mikilin.match.FieldModel;
import com.simonalong.mikilin.annotation.BlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:51
 */
@Data
@Accessors(chain = true)
public class IpEntity {

    @WhiteMatcher(model = FieldModel.IP_ADDRESS)
    private String ipValid;
    @BlackMatcher(model = FieldModel.IP_ADDRESS)
    private String ipInvalid;
}
