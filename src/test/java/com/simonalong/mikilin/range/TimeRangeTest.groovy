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
        date                             | time                                       | length | result
        getDate(2019, 7, 14, 00, 00, 00) | getDate(2019, 8, 4, 00, 00, 00).getTime()  | 150    | true
        getDate(2019, 7, 24, 00, 00, 00) | getDate(2019, 8, 4, 00, 00, 00).getTime()  | 150    | false
        getDate(2019, 7, 14, 00, 00, 00) | getDate(2019, 8, 14, 00, 00, 00).getTime() | 150    | false
        getDate(2019, 7, 14, 00, 00, 00) | getDate(2019, 8, 4, 00, 00, 00).getTime()  | 400    | false
    }

    /**
     * 测试解析现在
     * @return
     */
    // todo 有问题
    def "测试时间范围test2"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time)

        expect:
        boolean actResult = Checks.check("test2", range)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                             | time                                       | result
        getDate(2019, 8, 20, 00, 00, 00) | getDate(2019, 8, 10, 00, 00, 00).getTime() | true
        getDate(2019, 8, 24, 00, 00, 00) | getDate(2019, 8, 10, 00, 00, 00).getTime() | false
        getDate(2019, 8, 20, 00, 00, 00) | getDate(2019, 8, 14, 00, 00, 00).getTime() | false
    }

    /**
     * 测试解析过去
     * @return
     */
    // todo 有问题
    def "测试时间范围test3"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time)

        expect:
        boolean actResult = Checks.check("test3", range)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                             | time                                       | result
        getDate(2018, 8, 20, 00, 00, 00) | getDate(2019, 7, 10, 00, 00, 00).getTime() | true
        getDate(2019, 8, 4, 11, 56, 00)  | getDate(2019, 8, 4, 11, 56, 00).getTime()  | true
        getDate(2018, 8, 24, 00, 00, 00) | getDate(2019, 8, 10, 00, 00, 00).getTime() | false
        getDate(2019, 8, 20, 00, 00, 00) | getDate(2018, 8, 14, 00, 00, 00).getTime() | false
    }

    /**
     * 测试未来 future
     * @return
     */
    // todo 有问题
    def "测试时间范围test4"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time)

        expect:
        boolean actResult = Checks.check("test4", range)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                             | time                                       | result
        getDate(2019, 8, 20, 00, 00, 00) | getDate(2019, 9, 10, 00, 00, 00).getTime() | true
        getDate(2019, 8, 4, 12, 56, 00)  | getDate(2019, 8, 14, 11, 56, 00).getTime() | true
        getDate(2019, 8, 4, 11, 56, 00)  | getDate(2019, 8, 4, 11, 56, 00).getTime()  | false
        getDate(2018, 8, 24, 00, 00, 00) | getDate(2019, 7, 10, 00, 00, 00).getTime() | false
        getDate(2019, 8, 20, 00, 00, 00) | getDate(2018, 7, 14, 00, 00, 00).getTime() | false
    }

    /**
     * 测试未来 future
     * @return
     */
    def "测试时间范围test5"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setTime(time)

        expect:
        boolean actResult = Checks.check("test5", range)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        time  | result
        200   | true
        100   | true
        99    | false
        30001 | false
    }

    /**
     * 测试过去 ('xxx', now)
     * @return
     */
    def "测试时间范围test6"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setTime(time)

        expect:
        boolean actResult = Checks.check("test6", range)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        time                                       | result
        getDate(2019, 7, 20, 00, 00, 00).getTime() | true
        getDate(2019, 7, 24, 2, 7, 00).getTime()   | true
        getDate(2019, 9, 4, 12, 06, 00).getTime()  | false
    }

    /**
     * 测试过去 (null, now)
     * @return
     */
    def "测试时间范围test7"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setTime(time)

        expect:
        boolean actResult = Checks.check("test7", range)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        time                                       | result
        getDate(2019, 7, 20, 00, 00, 00).getTime() | true
        getDate(2019, 7, 24, 2, 7, 00).getTime()   | true
        getDate(2019, 9, 4, 12, 06, 00).getTime()  | false
    }

    /**
     * 测试带上特殊的字符 ('null', 'now')
     * @return
     */
    def "测试时间范围test8"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setTime(time)

        expect:
        boolean actResult = Checks.check("test8", range)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        time                                       | result
        getDate(2019, 7, 20, 00, 00, 00).getTime() | true
        getDate(2019, 7, 24, 2, 7, 00).getTime()   | true
        getDate(2019, 9, 4, 12, 06, 00).getTime()  | false
    }

    def getDate(def year, def month, def day, def hour, def minute, def second) {
        return Date.from(LocalDateTime.of(year, month, day, hour, minute, second).atZone(ZoneId.systemDefault()).toInstant())
    }
}
