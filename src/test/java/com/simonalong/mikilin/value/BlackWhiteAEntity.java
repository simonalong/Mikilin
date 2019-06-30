package com.simonalong.mikilin.value;

import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/1/5 下午6:24
 */
@Data
@Accessors(chain = true)
public class BlackWhiteAEntity {

    @FieldWhiteMatcher({"a","b"})
    private String name;
    @FieldBlackMatcher({"1","2"})
    private Integer age;
}
