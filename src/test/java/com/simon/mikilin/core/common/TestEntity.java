package com.simon.mikilin.core.common;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/6/12 下午10:04
 */
@Data
@Accessors(chain = true)
public class TestEntity {

    @FieldBlackMatcher({"nihao", "ok"})
    private String name;
    @FieldWhiteMatcher(range = "[12, 32]")
    private Integer age;
    @FieldWhiteMatcher({"beijing", "shanghai"})
    private String address;
}
