package com.simonalong.mikilin.isBlank

import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.exception.MkException
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2020/6/18 2:50 PM
 */
class IsBlankMatcherTest extends Specification {

    def "为null测试"() {
        given:
        IsBlankEntity entity = new IsBlankEntity().setName(name)

        expect:
        boolean actResult = MkValidators.check(entity, "name")
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name | result
        "a"  | false
        null | true
        null | true
        ""   | true
        "a"  | false
    }

    def "notBlank修饰非String异常"() {
        given:
        IsBlankEntity2 entity = new IsBlankEntity2().setAge(123)

        when:
        MkValidators.check(entity)

        then:
        thrown(MkException)
    }

    def "blank测试"() {
        given:
        IsBlankEntity3 entity = new IsBlankEntity3().setName(name)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name | result
        "a"  | true
        null | false
        null | false
        ""   | false
        "a"  | true
    }

    def "blank测试2"() {
        given:
        IsBlankEntity4 entity = new IsBlankEntity4().setName(name)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name | result
        "a"  | false
        null | true
        null | true
        ""   | true
        "a"  | false
    }
}
