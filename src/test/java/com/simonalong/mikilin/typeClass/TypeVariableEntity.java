package com.simonalong.mikilin.typeClass;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author shizi
 * @since 2020/3/24 下午2:09
 */
@Data
@Accessors(chain = true)
public class TypeVariableEntity<T> {

    private Integer pageNo;
    @Matcher(range = "[0, 100]", value = "null")
    private Integer pageSize;

    @Check
    private T data;

    @Check
    private List<T> dataList;
}
