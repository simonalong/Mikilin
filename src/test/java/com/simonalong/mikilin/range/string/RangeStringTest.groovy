package com.simonalong.mikilin.range.string

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2020/7/9 7:56 PM
 */
class RangeStringTest extends Specification {

    def "长度测试"() {
        given:
        StringRangeEntity1 range = new StringRangeEntity1().setData(data)

        expect:
        boolean actResult = MkValidators.check(range)
        if (!actResult) {
            println MkValidators.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        data   | result
        "a"    | true
        'a'    | true
        "ab"   | true
        "abc"  | true
        "abcd" | false
    }
}
