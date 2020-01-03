package com.simonalong.mikilin.value.bool

import com.simonalong.mikilin.Checks
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/10/26 下午9:14
 */
class BooleanValueTest extends Specification {

    def "boolean类型测试"(){
        given:
        BooleanEntity entity = new BooleanEntity()
        entity.setFlag(flag)

        expect:
        boolean actResult = Checks.check(entity)
        if (!actResult) {
            println Checks.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        flag  | result
        true  | true
        false | false
        null  | true
    }
}
