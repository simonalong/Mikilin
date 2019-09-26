package com.simonalong.mikilin.type;

import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/15 下午10:53
 */
@Data
@Accessors(chain = true)
public class TypeEntity {

    @WhiteMatcher(type = Integer.class)
    private Integer data;

    @WhiteMatcher(type = CharSequence.class)
    private String name;

    @WhiteMatcher(type = {Integer.class, Float.class})
    private Object obj;

    @WhiteMatcher(type = Number.class)
    private Object num;
}
