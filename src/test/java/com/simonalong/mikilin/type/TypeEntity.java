package com.simonalong.mikilin.type;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.common.TestEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/15 下午10:53
 */
@Data
@Accessors(chain = true)
public class TypeEntity {

    /**
     * 没有必要设置type
     */
    @Matcher(type = Integer.class)
    private Integer data;

    @Matcher(type = String.class)
    private CharSequence name;

    @Matcher(type = {Integer.class, Float.class})
    private Object obj;

    @Matcher(type = Number.class)
    private Object num;

    @Matcher(type = TypeErrEntity.class)
    private Integer errStr;
}
