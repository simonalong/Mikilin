package com.simonalong.mikilin.range;

import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatchers;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/8/3 下午5:52
 */
@Data
@Accessors(chain = true)
public class RangeTimeEntity1 {

    @FieldWhiteMatchers({
        @FieldWhiteMatcher(group = "test1", range = "['2019-08-03 12:00:23.321', '2019-08-13 12:00:23.321']"),
        @FieldWhiteMatcher(group = "test2", range = "(now, '2019-08-13 12:00:23.321']"),
        @FieldWhiteMatcher(group = "test3", range = "past"),
        @FieldWhiteMatcher(group = "test3", range = "future"),
    })
    private Date date;
    @FieldWhiteMatchers({
        @FieldWhiteMatcher(group = "test1", range = "['2019-08-03 12:00:23.321', '2019-08-13 12:00:23.321']"),
        @FieldWhiteMatcher(group = "test2", range = "(now, '2019-08-13 12:00:23.321']"),
        @FieldWhiteMatcher(group = "test3", range = "past"),
        @FieldWhiteMatcher(group = "test3", range = "future"),
        @FieldWhiteMatcher(group = "test4", range = "[100, 30000]"),
    })
    private Long time;
    @FieldWhiteMatchers({
        @FieldWhiteMatcher(group = "test1", range = "[100, 200]"),
    })
    private Long length;
}
