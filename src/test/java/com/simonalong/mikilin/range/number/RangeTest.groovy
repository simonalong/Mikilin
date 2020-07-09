package com.simonalong.mikilin.range.number

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/4/14 上午11:01
 */
class RangeTest extends Specification {

    def "范围的内部空格测试"() {
        given:
        RangeEntity1 range = new RangeEntity1()
        range.setAge1(age1)
        range.setAge2(age2)

        expect:
        boolean actResult = MkValidators.check(range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        age1 | age2 | result
        0    | 0    | true
        10   | 10   | true
        100  | 100  | true
        200  | 10   | false
        10   | 210  | false
        null | 210  | false
    }

    def "范围的边界测试"() {
        given:
        RangeEntity2 range = new RangeEntity2();
        range.setAge3(age3)
        range.setAge4(age4)
        range.setAge5(age5)

        expect:
        boolean actResult = MkValidators.check(range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        age3 | age4 | age5 | result
        0    | 0    | 0    | false
        1    | 0    | 0    | false
        1    | 0    | 1    | true
        10   | 50   | 15   | true
        100  | 99   | 99   | true
        100  | 100  | 100  | false
        300  | 200  | 101  | false
    }

    def "范围的字段黑名单测试"() {
        given:
        RangeEntity3 range = new RangeEntity3()
        range.setHeight(height)
        range.setMoney(money)

        expect:
        boolean actResult = MkValidators.check(range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        height | money | result
        0.00   | 0     | true
        2.99   | 9     | true
        2.99   | 10    | false
        2.99   | 9999  | false
        2.99   | 10000 | false
        2.99   | 10001 | true
        3.00   | 10001 | true
        3.01   | 10001 | false
    }

    def "范围的无限字段测试1"() {
        given:
        RangeEntity4 range = new RangeEntity4()
        range.setNum1(num1)
        range.setNum2(num2)
        range.setNum3(num3)
        range.setNum4(num4)

        expect:
        boolean actResult = MkValidators.check(range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        num1 | num2 | num3 | num4 | result
        101  | 100  | 49   | 50   | true
        100  | 100  | 49   | 50   | false
        101  | 90   | 49   | 50   | false
        101  | 100  | 50   | 50   | false
        101  | 100  | 50   | 51   | false
    }

    def "范围的无限字段测试2"() {
        given:
        RangeEntity5 range = new RangeEntity5()
        range.setNum1(num1)
        range.setNum2(num2)
        range.setNum3(num3)
        range.setNum4(num4)

        expect:
        boolean actResult = MkValidators.check(range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
            println MkValidators.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        num1 | num2 | num3 | num4 | result
        101  | 100  | 49   | 50   | true
        100  | 100  | 49   | 50   | false
        101  | 90   | 49   | 50   | false
        101  | 100  | 50   | 50   | false
        101  | 100  | 50   | 51   | false
    }
}
