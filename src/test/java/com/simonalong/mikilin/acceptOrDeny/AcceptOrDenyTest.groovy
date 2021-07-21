package com.simonalong.mikilin.acceptOrDeny

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/6/12 下午10:03
 */
class AcceptOrDenyTest extends Specification {

    def "测试指定的属性name"() {
        given:
        AcceptEntity entity = new AcceptEntity().setName(name).setAge(age)

        expect:
        def act = MkValidators.check(entity)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        name | age | result
        "a"  | 0   | true
        "b"  | 89  | true
        "c"  | 100 | false
        null | 200 | false
        "d"  | 0   | false
    }

    def "测试指定的属性age"() {
        given:
        DenyEntity entity = new DenyEntity().setName(name).setAge(age)

        expect:
        def act = MkValidators.check(entity)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        name | age | result
        "a"  | 0   | false
        "b"  | 89  | false
        "c"  | 100 | false
        null | 200 | false
        "d"  | 0   | false
        "d"  | 200 | true
    }
}
