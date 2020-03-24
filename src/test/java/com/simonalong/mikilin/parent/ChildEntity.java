package com.simonalong.mikilin.parent;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/3/25 上午2:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ChildEntity extends ParentEntity{

    @Matcher(range = "[0, 100]")
    private Integer self;
}
