package com.simonalong.mikilin.condition;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author shizi
 * @since 2020/6/28 5:04 PM
 */
@Data
@Accessors(chain = true)
public class ConditionEntity5 {

    private Integer handleType;

    @Matcher(condition = "(#current == null && #root.handleType != 1) || (#current != null && !#current.isEmpty() && #root.handleType == 1)", errMsg = "cantEditColumnList 需要在handleType为1的时候才有值")
    private List<String> nameList;
}
