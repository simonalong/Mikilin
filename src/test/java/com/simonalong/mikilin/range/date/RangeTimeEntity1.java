package com.simonalong.mikilin.range.date;

import com.simonalong.mikilin.annotation.WhiteMatcher;
import com.simonalong.mikilin.annotation.WhiteMatchers;
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

    @WhiteMatchers({
        @WhiteMatcher(group = "test1", range = "['2019-07-13 12:00:23.321', '2019-07-23 12:00:23.321']"),
        @WhiteMatcher(group = "test2", range = "(now, '2199-08-23 12:00:23.321']"),
        @WhiteMatcher(group = "test3", range = "past"),
        @WhiteMatcher(group = "test4", range = "future"),
    })
    private Date date;

    @WhiteMatchers({
        @WhiteMatcher(group = "test1", range = "['2019-08-03 12:00:23.321', '2019-08-13 12:00:23.321']"),
        @WhiteMatcher(group = "test2", range = "(now, '2199-08-13 12:00:23.321']"),
        @WhiteMatcher(group = "test3", range = "past"),
        @WhiteMatcher(group = "test4", range = "future"),
        @WhiteMatcher(group = "test5", range = "[100, 30000]"),
        @WhiteMatcher(group = "test6", range = "('2019-07-13 12:00:23.321', now]"),
        @WhiteMatcher(group = "test7", range = "(null, now)"),
        @WhiteMatcher(group = "test8", range = "('null', 'now')"),
        @WhiteMatcher(group = "test9", range = "['2019-08', '2019-08-13 12:00:23.321']"),
    })
    private Long time;

    @WhiteMatchers({
        @WhiteMatcher(group = "test1", range = "[100, 200]"),
    })
    private Long length;
}
