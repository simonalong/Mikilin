package com.simonalong.mikilin.range.date

import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.util.LocalDateTimeUtil
import org.junit.Assert
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.regex.Pattern

/**
 * @author zhouzhenyong
 * @since 2019/8/3 下午5:52
 */
class TimeRangeTest extends Specification {

    def "测试时间范围test1"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time).setLength(length);

        expect:
        boolean actResult = MkValidators.check("test1", range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                             | time                                       | length | result
        getDate(2019, 7, 14, 00, 00, 00) | getDate(2019, 8, 4, 00, 00, 00).getTime()  | 150    | true
        getDate(2019, 7, 24, 00, 00, 00) | getDate(2019, 8, 4, 00, 00, 00).getTime()  | 150    | false
        getDate(2019, 7, 14, 00, 00, 00) | getDate(2019, 8, 14, 00, 00, 00).getTime() | 150    | false
        getDate(2019, 7, 14, 00, 00, 00) | getDate(2019, 8, 4, 00, 00, 00).getTime()  | 400    | false
        null | getDate(2019, 8, 4, 00, 00, 00).getTime()  | 400    | false
    }

    /**
     * 测试解析现在
     * @return
     */
    def "测试时间范围test2"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time)

        expect:
        boolean actResult = MkValidators.check("test2", range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                             | time                                       | result
        getDate(2099, 8, 20, 00, 00, 00) | getDate(2099, 8, 10, 00, 00, 00).getTime() | true
        getDate(2009, 8, 24, 00, 00, 00) | getDate(2099, 8, 10, 00, 00, 00).getTime() | false
        getDate(2099, 8, 20, 00, 00, 00) | getDate(2009, 8, 14, 00, 00, 00).getTime() | false
        getDate(2009, 8, 20, 00, 00, 00) | getDate(2009, 8, 14, 00, 00, 00).getTime() | false
    }

    /**
     * 测试解析过去
     * @return
     */
    def "测试时间范围test3"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time)

        expect:
        boolean actResult = MkValidators.check("test3", range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                             | time                                       | result
        getDate(2008, 8, 20, 00, 00, 00) | getDate(2009, 7, 10, 00, 00, 00).getTime() | true
        getDate(2099, 8, 24, 00, 00, 00) | getDate(2009, 8, 10, 00, 00, 00).getTime() | false
        getDate(2009, 8, 20, 00, 00, 00) | getDate(2099, 8, 14, 00, 00, 00).getTime() | false
        getDate(2099, 8, 20, 00, 00, 00) | getDate(2099, 8, 14, 00, 00, 00).getTime() | false
    }

    /**
     * 测试未来 future
     * @return
     */
    def "测试时间范围test4"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time)

        expect:
        boolean actResult = MkValidators.check("test4", range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                             | time                                       | result
        getDate(2099, 8, 20, 00, 00, 00) | getDate(2099, 9, 10, 00, 00, 00).getTime() | true
        getDate(2009, 8, 4, 12, 56, 00)  | getDate(2099, 8, 14, 11, 56, 00).getTime() | false
        getDate(2099, 8, 4, 11, 56, 00)  | getDate(2009, 8, 4, 11, 56, 00).getTime()  | false
        getDate(2009, 8, 24, 00, 00, 00) | getDate(2009, 7, 10, 00, 00, 00).getTime() | false
    }

    /**
     * 测试未来 future
     * @return
     */
    def "测试时间范围test5"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setTime(time)

        expect:
        boolean actResult = MkValidators.check("test5", range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        time  | result
        200L   | true
        100L   | true
        99L    | false
        30001L | false
    }

    /**
     * 测试过去 ('xxx', now)
     * @return
     */
    def "测试时间范围test6"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setTime(time)

        expect:
        boolean actResult = MkValidators.check("test6", range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        time                                       | result
        getDate(2019, 7, 20, 00, 00, 00).getTime() | true
        getDate(2019, 7, 24, 2, 7, 00).getTime()   | true
        getDate(2119, 9, 4, 12, 06, 00).getTime()  | false
    }

    /**
     * 测试过去 (null, now)
     * @return
     */
    def "测试时间范围test7"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setTime(time)

        expect:
        boolean actResult = MkValidators.check("test7", range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        time                                       | result
        getDate(2019, 7, 20, 00, 00, 00).getTime() | true
        getDate(2019, 7, 24, 2, 7, 00).getTime()   | true
        getDate(2119, 9, 4, 12, 06, 00).getTime()  | false
    }

    /**
     * 测试带上特殊的字符 ('null', 'now')
     * @return
     */
    def "测试时间范围test8"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setTime(time)

        expect:
        boolean actResult = MkValidators.check("test8", range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        time                                       | result
        getDate(2019, 7, 20, 00, 00, 00).getTime() | true
        getDate(2019, 7, 24, 2, 7, 00).getTime()   | true
        getDate(2119, 9, 4, 12, 06, 00).getTime()  | false
    }

    /**
     * 测试带上特殊的字符 ('null', 'now')
     * @return
     */
    def "测试时间范围test9"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setTime(time)

        expect:
        boolean actResult = MkValidators.check("test9", range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        time                                       | result
        getDate(2019, 8, 01, 00, 00, 00).getTime() | true
        getDate(2019, 7, 31, 2, 7, 00).getTime()   | false
        getDate(2019, 8, 01, 00, 00, 00).getTime()  | true
    }

    /**
     * 测试时间计算的时间范围合法性 (-1d,)：过去一天内
     * @return
     */
    def "测试时间变动test10"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time)

        expect:
        boolean actResult = MkValidators.check("test10", range)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                            | time                                      | result
        getDatePlus(0, 0, 0, 0, 0, 0)   | getDatePlus(0, 0, 0, 0, 0, 0).getTime()   | true
        getDatePlus(0, 0, 1, 0, 0, 0)   | getDatePlus(0, 0, 0, 0, 0, 0).getTime()   | true
        getDatePlus(0, 0, 0, 0, 0, 0)   | getDatePlus(0, 0, 1, 0, 0, 0).getTime()   | true
        getDatePlus(0, 0, 0, -1, 0, 0)  | getDatePlus(0, 0, 0, -1, 0, 0).getTime()  | true
        getDatePlus(0, 0, -1, -1, 0, 0) | getDatePlus(0, 0, 0, -1, 0, 0).getTime()  | false
        getDatePlus(0, 0, 0, 0, 1, 0)   | getDatePlus(0, 0, 0, -1, 0, 0).getTime()  | true
        getDatePlus(0, 0, 0, -1, 0, 0)  | getDatePlus(0, 0, -1, -1, 0, 0).getTime() | false
        getDatePlus(0, 0, 0, -1, 0, 0)  | getDatePlus(0, 0, 0, 1, 0, 0).getTime()   | true
    }

    /**
     * 测试时间计算的时间范围合法性(-2h, +4d)：过去2小时以及未来4天内，时间(-3d2h,)：过去3天2小时内
     * @return
     */
    def "测试时间变动test11"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date).setTime(time)

        expect:
        boolean actResult = MkValidators.check("test11", range)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                            | time                                      | result
        getDatePlus(0, 0, 0, 0, -1, 0)  | getDatePlus(0, 0, 0, 0, 0, 0).getTime()   | true
        getDatePlus(0, 0, 0, -3, 0, 0)  | getDatePlus(0, 0, 0, 0, 0, 0).getTime()   | false
        getDatePlus(0, 0, 0, -2, -1, 0) | getDatePlus(0, 0, 0, 0, 0, 0).getTime()   | false
        getDatePlus(0, 0, 0, -2, 1, 0)  | getDatePlus(0, 0, 0, 0, 0, 0).getTime()   | true
        getDatePlus(0, 0, 3, 0, 0, 0)   | getDatePlus(0, 0, 0, 0, 0, 0).getTime()   | true
        getDatePlus(0, 0, 5, 0, 0, 0)   | getDatePlus(0, 0, 0, 0, 0, 0).getTime()   | false
        getDatePlus(0, 0, 0, 0, 0, 0)   | getDatePlus(0, 0, 1, 0, 0, 0).getTime()   | true
        getDatePlus(0, 0, 0, 0, 0, 0)   | getDatePlus(0, 0, -1, 0, 0, 0).getTime()  | true
        getDatePlus(0, 0, 0, 0, 0, 0)   | getDatePlus(0, 0, -3, -3, 0, 0).getTime() | false
        getDatePlus(0, 0, 0, 0, 0, 0)   | getDatePlus(0, 0, -3, -1, 0, 0).getTime() | true
    }

    /**
     * 测试时间计算的时间范围合法性(-4d, -2d)：过去4小时到过去两天内
     * @return
     */
    def "测试时间变动test12"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date)

        expect:
        boolean actResult = MkValidators.check("test12", range)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                            | result
        getDatePlus(0, 0, 0, 0, -1, 0)  | true
        getDatePlus(0, 0, 0, -3, 0, 0)  | false
        getDatePlus(0, 0, 0, -2, -1, 0) | false
        getDatePlus(0, 0, 0, -2, 1, 0)  | true
        getDatePlus(0, 0, 3, 0, 0, 0)   | true
        getDatePlus(0, 0, 5, 0, 0, 0)   | false
        getDatePlus(0, 0, 0, 0, 0, 0)   | true
        getDatePlus(0, 0, 0, 0, 0, 0)   | true
        getDatePlus(0, 0, 0, 0, 0, 0)   | true
        getDatePlus(0, 0, 0, 0, 0, 0)   | true
    }

    /**
     * 测试时间计算的时间范围合法性(-4d, -2d)：过去4小时到过去两天内
     * @return
     */
    def "测试时间变动test13"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date)

        expect:
        boolean actResult = MkValidators.check("test13", range)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                            | result
        getDatePlus(0, 0, 0, 0, 0, 0)   | false
        getDatePlus(0, 0, -1, 0, 0, 0)  | false
        getDatePlus(0, 0, -2, 1, 0, 0)  | false
        getDatePlus(0, 0, -2, -1, 0, 0) | true
        getDatePlus(0, 0, -3, 0, 0, 0)  | true
        getDatePlus(0, 0, -4, 1, 0, 0)  | true
        getDatePlus(0, 0, -4, -1, 0, 0) | false
        getDatePlus(0, 0, -5, 0, 0, 0)  | false
    }

    /**
     * 测试时间计算的时间范围合法性(-4y2M5d3h2m3s,)：过去4年2个月5天3小时2分钟3秒内
     * @return
     */
    def "测试时间变动test14"() {
        given:
        RangeTimeEntity1 range = new RangeTimeEntity1().setDate(date)

        expect:
        boolean actResult = MkValidators.check("test14", range)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        date                                | result
        getDatePlus(0, 0, 0, 0, 0, 0)       | true
        getDatePlus(-4, -2, -5, -3, -2, -2) | true
        getDatePlus(-4, -2, -5, -3, -2, -3) | false
        getDatePlus(-4, -2, -5, -3, -3, 0)  | false
        getDatePlus(-5, 0, 0, 0, 0, 0)      | false
    }

    def getDatePlus(def years, def months, def days, def hours, def minutes, def seconds) {
        return LocalDateTimeUtil.plusTime(new Date(), years, months, days, hours, minutes, seconds);
    }

    def getDate(def year, def month, def day, def hour, def minute, def second) {
        return Date.from(LocalDateTime.of(year, month, day, hour, minute, second).atZone(ZoneId.systemDefault()).toInstant())
    }
}
