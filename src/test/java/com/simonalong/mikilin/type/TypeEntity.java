package com.simonalong.mikilin.type;

import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/15 下午10:53
 */
@Data
@Accessors(chain = true)
public class TypeEntity {

    @FieldWhiteMatcher(type = Integer.class)
    private Integer data;

    @FieldWhiteMatcher(type = CharSequence.class)
    private String name;

    @FieldWhiteMatcher(type = {Integer.class, Float.class})
    private Object obj;

    @FieldWhiteMatcher(type = Number.class)
    private Object num;
}
