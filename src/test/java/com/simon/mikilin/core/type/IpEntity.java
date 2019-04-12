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
public class IpEntity {

    @FieldValidCheck(type = FieldType.IP_ADDRESS)
    private String ipValid;
    @FieldInvalidCheck(type = FieldType.IP_ADDRESS)
    private String ipInvalid;
}
