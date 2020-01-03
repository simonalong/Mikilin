package com.simonalong.mikilin.value.num

import com.simonalong.mikilin.Checks
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/10/26 下午9:15
 */
class IntegerValueTest extends Specification {

    def "integer类型的判断"(){
        given:
        NumberEntity entity = new NumberEntity()
        entity.setAge(age)

        expect:
        boolean actResult = Checks.check(entity)
        if (!actResult) {
            println Checks.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        age | result
        1    | true
        2    | true
        null | true
        3    | false
    }
}
