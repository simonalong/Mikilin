package com.simonalong.mikilin.typeClass;

import com.simonalong.mikilin.annotation.Check;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.AfterClass;

/**
 * @author shizi
 * @since 2020/3/24 下午6:54
 */
@Data
@Accessors(chain = true)
public class GenericArrayTypeEntity<T> {

    @Check
    private T[] dataArray;

    @Check
    private T[][] dataArrays;
}
