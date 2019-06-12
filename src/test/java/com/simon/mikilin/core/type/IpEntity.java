package com.simon.mikilin.core.type;

import com.simon.mikilin.core.annotation.FieldWhiteMather;
import com.simon.mikilin.core.match.FieldType;
import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:51
 */
@Data
@Accessors(chain = true)
public class IpEntity {

    @FieldWhiteMather(type = FieldType.IP_ADDRESS)
    private String ipValid;
    @FieldBlackMatcher(type = FieldType.IP_ADDRESS)
    private String ipInvalid;
}
