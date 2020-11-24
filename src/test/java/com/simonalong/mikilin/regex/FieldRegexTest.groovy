package com.simonalong.mikilin.regex

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong* @since 2019/3/10 下午10:16
 */
class FieldRegexTest extends Specification {

    def "正则表达式测试"() {
        given:
        RegexEntity entity = new RegexEntity().setRegexValid(valid).setRegexInValid(invalid)

        expect:
        boolean actResult = MkValidators.check(entity, "regexValid", "regexInValid")
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
        null        | "3312312" | false
    }

    def "版本号测试"() {
        given:
        RegexEntity entity = new RegexEntity().setVersion(version)

        expect:
        boolean actResult = MkValidators.check(entity, "version")
        if (!result) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        version     | result
        "adfs12312" | false
        "a.b"       | false
        "1..2"      | false
        "1.d."      | false
        "1.d.a"     | true
        "v1.0.0"    | true
    }
}
