package com.simonalong.mikilin.common;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/6/12 下午10:04
 */
@Data
@Accessors(chain = true)
public class TestEntity {

    @Matcher(value = {"nihao", "ok"}, accept = false)
    private String name;
    @Matcher(range = "[12, 32]")
    private Integer age;
    @Matcher({"beijing", "shanghai"})
    private String address;
}
