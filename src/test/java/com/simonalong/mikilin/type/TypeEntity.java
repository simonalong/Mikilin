package com.simonalong.mikilin.type;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/15 下午10:53
 */
@Data
@Accessors(chain = true)
public class TypeEntity {

    @Matcher(type = Integer.class)
    private Integer data;

    @Matcher(type = CharSequence.class)
    private String name;

    @Matcher(type = {Integer.class, Float.class})
    private Object obj;

    @Matcher(type = Number.class)
    private Object num;
}
