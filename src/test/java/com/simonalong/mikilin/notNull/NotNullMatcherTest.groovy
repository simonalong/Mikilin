package com.simonalong.mikilin.notNull

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2020/6/18 2:33 PM
 */
class NotNullMatcherTest extends Specification {

    def "不null测试"() {
        given:
        NotNullEntity entity = new NotNullEntity().setName(name).setAge(age)

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
        null | null | false
        null | 5    | false
        "a"  | 3    | true
    }

    def "null测试"() {
        given:
        NotNullEntity2 entity = new NotNullEntity2().setName(name).setAge(age)

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
        null | 5    | false
        "a"  | 3    | false
    }
}
