package com.simon.mikilin.core.value;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMatcher;
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
