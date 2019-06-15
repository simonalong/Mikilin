package com.github.simonalong.mikilin.type;

import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.github.simonalong.mikilin.match.FieldType;
import com.github.simonalong.mikilin.annotation.FieldBlackMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午9:51
 */
@Data
@Accessors(chain = true)
public class IpEntity {

    @FieldWhiteMatcher(type = FieldType.IP_ADDRESS)
    private String ipValid;
    @FieldBlackMatcher(type = FieldType.IP_ADDRESS)
    private String ipInvalid;
}
