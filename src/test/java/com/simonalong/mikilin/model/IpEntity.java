package com.simonalong.mikilin.model;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.match.FieldModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:51
 */
@Data
@Accessors(chain = true)
public class IpEntity {

    @Matcher(model = FieldModel.IP_ADDRESS)
    private String ipValid;
    @Matcher(model = FieldModel.IP_ADDRESS, acceptOrDeny = false)
    private String ipInvalid;
}
