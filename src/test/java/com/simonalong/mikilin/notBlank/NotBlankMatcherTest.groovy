package com.simonalong.mikilin.notBlank

import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.exception.MkException
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2020/6/18 2:50 PM
 */
class NotBlankMatcherTest extends Specification {

    def "不null测试"() {
        given:
        NotBlankEntity entity = new NotBlankEntity().setName(name).setAge(age)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name | age  | result
        "a"  | null | true
        null | null | false
        null | 5    | false
        ""   | 3    | false
        "a"  | 3    | true
    }

    def "notBlank修饰非String异常"() {
        given:
        NotBlankEntity2 entity = new NotBlankEntity2().setName("asdf").setAge(123)

        when:
        MkValidators.check(entity)

        then:
        thrown(MkException)
    }


    def "null测试"() {
        given:
        NotBlankEntity3 entity = new NotBlankEntity3().setName(name).setAge(age)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name | age  | result
        "a"  | null | false
        null | null | true
        null | 5    | true
        ""   | 3    | true
        "a"  | 3    | false
    }
}
