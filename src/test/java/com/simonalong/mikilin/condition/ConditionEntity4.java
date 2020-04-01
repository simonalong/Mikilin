package com.simonalong.mikilin.condition;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shizi
 * @since 2020/4/1 11:20 AM
 */
@Data
@Accessors(chain = true)
public class ConditionEntity4 {

    /**
     * 类型只可为：0，1，2
     */
    @Matcher(range = "[0, 2]")
    private Integer type;
    /**
     * 只有type的值为1和2的时候，该值才生效，也就是不能为空，而且值的范围必须在a, b中选择一个
     */
    @Matcher(condition = "if(#root.type==1 || #root.type==2){return (#current.equals(\"a\") || #current.equals(\"b\"))}else{return true}")
    private String name;
}
