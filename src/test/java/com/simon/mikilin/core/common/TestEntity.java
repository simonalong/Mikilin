package com.simon.mikilin.core.common;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMather;
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
    @FieldWhiteMather(range = "[12, 32]")
    private Integer age;
    @FieldWhiteMather({"beijing", "shanghai"})
    private String address;
}
