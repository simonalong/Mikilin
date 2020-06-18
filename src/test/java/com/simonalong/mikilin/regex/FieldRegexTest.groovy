package com.simonalong.mikilin.regex

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:16
 */
class FieldRegexTest extends Specification {

    def "正则表达式测试"() {
        given:
        RegexEntity entity = new RegexEntity().setRegexValid(valid).setRegexInValid(invalid)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!result) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        valid       | invalid   | result
        "adfs12312" | "3312312" | false
        "asdf"      | "asf"     | false
        "3312312"   | "sdf"     | true
        "3312312"   | "3312312" | false
        null   | "3312312" | false
    }
}
