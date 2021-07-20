package com.simonalong.mikilin.isNull.notNull

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2020/6/18 2:33 PM
 */
class IsNullMatcherTest extends Specification {

    def "isNull测试"() {
        given:
        IsNullEntity entity = new IsNullEntity().setName(name).setAge(age)

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
}
