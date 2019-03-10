package com.simon.mikilin.core.white;

import com.simon.mikilin.core.annotation.Check;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import com.simon.mikilin.core.value.BEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:44
 */
@Data
@Accessors(chain = true)
public class WhiteBEntity {

    @Check
    private BEntity bEntity;
    @FieldValidCheck({"a", "b"})
    private String name;
}
