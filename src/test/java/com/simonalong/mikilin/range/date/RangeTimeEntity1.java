package com.simonalong.mikilin.range.date;

import com.simonalong.mikilin.annotation.Matcher;
import com.simonalong.mikilin.annotation.Matchers;

import java.time.LocalDateTime;
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

    @Matchers({
        @Matcher(group = "test1", range = "['2019-07-13 12:00:23.321', '2019-07-23 12:00:23.321']"),
        @Matcher(group = "test2", range = "(now, '2199-08-23 12:00:23.321']"),
        @Matcher(group = "test3", range = "past"),
        @Matcher(group = "test4", range = "future"),
        // 一天内的时间
        @Matcher(group = "test10", range = "(-1d,)"),
        // 两小时内的到未来4天内时间
        @Matcher(group = "test11", range = "(-2h, +4d)"),
        @Matcher(group = "test12", range = "(-2h, 4d)"),
        // 过去四天到过去两天之间
        @Matcher(group = "test13", range = "(-4d, -2d)"),
        // 过去四年2月5天3小时2分钟3秒
        @Matcher(group = "test14", range = "(-4y2M5d3h2m3s,)"),
        @Matcher(group = "test15", range = "(,3d)"),
    })
    private Date date;

    @Matchers({
        @Matcher(group = "test1", range = "['2019-08-03 12:00:23.321', '2019-08-13 12:00:23.321']"),
        @Matcher(group = "test2", range = "(now, '2199-08-13 12:00:23.321']"),
        @Matcher(group = "test3", range = "past"),
        @Matcher(group = "test4", range = "future"),
        @Matcher(group = "test5", range = "[100, 30000]"),
        @Matcher(group = "test6", range = "('2019-07-13 12:00:23.321', now]"),
        @Matcher(group = "test7", range = "(null, now)"),
        @Matcher(group = "test8", range = "('null', 'now')"),
        @Matcher(group = "test9", range = "['2019-08', '2019-08-13 12:00:23.321']"),
        // 一天内的时间
        @Matcher(group = "test10", range = "(-1d,)"),
        // 三天两小时内的时间
        @Matcher(group = "test11", range = "(-3d2h,)"),
        // 下面这个报异常，右边时间不能小于左边时间
        // @Matcher(group = "test15", range = "(, -3d)"),
    })
    private Long time;

    @Matchers({
        @Matcher(group = "test1", range = "[100, 200]"),
    })
    private Long length;
}
