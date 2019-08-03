package com.simonalong.mikilin.range

import com.simonalong.mikilin.Checks
import com.simonalong.mikilin.annotation.FieldWhiteMatcher
import com.simonalong.mikilin.annotation.FieldWhiteMatchers
import org.junit.Assert
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.ZoneId

/**
 * @author zhouzhenyong
 * @since 2019/8/3 下午5:52
 */
class TimeRangeTest extends Specification {

    def "测试时间范围test1"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time).setLength(length);

        expect:
        boolean actResult = Checks.check("test1", range)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                            | time                                      | length | result
//        getDate(2019, 8, 4, 00, 00, 00) | getDate(2019, 8, 4, 00, 00, 00).getTime() | 150    | true
        getDate(2019, 8, 14, 00, 00, 00) | getDate(2019, 8, 4, 00, 00, 00).getTime() | 150    | false
//        getDate(2019, 8, 4, 00, 00, 00) | getDate(2019, 8, 14, 00, 00, 00).getTime() | 150    | false
//        getDate(2019, 8, 4, 00, 00, 00) | getDate(2019, 8, 4, 00, 00, 00).getTime() | 400    | false
    }

    def getDate(def year, def month, def day, def hour, def minute, def second) {
        return Date.from(LocalDateTime.of(year, month, day, hour, minute, second).atZone(ZoneId.systemDefault()).toInstant())
    }
}
