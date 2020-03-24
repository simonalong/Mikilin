package com.simonalong.mikilin.typeClass;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.AfterClass;

/**
 * @author shizi
 * @since 2020/3/24 下午6:50
 */
@Data
@Accessors(chain = true)
public class ChildDataEntity extends DataEntity {

    @Matcher(value = {"a", "b"})
    private String nameChild;
}
