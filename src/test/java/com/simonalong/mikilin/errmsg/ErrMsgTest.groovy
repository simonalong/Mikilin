package com.simonalong.mikilin.errmsg

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2020/3/17 下午11:46
 */
class ErrMsgTest extends Specification {

    def "测试白名单情况下的返回"() {
        given:
        ErrMsgEntity entity = new ErrMsgEntity().setAge(age)

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            println MkValidators.getErrMsg()
        }
        Assert.assertEquals(result, act)

        where:
        age  | result
        12   | true
        100  | true
        200  | false
        null | false
    }

    def "测试黑名单情况下的返回"() {
        given:
        ErrMsgEntity2 entity = new ErrMsgEntity2().setAge(age)

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            println MkValidators.getErrMsg()
        }
        Assert.assertEquals(result, act)

        where:
        age  | result
        12   | true
        100  | true
        201  | false
        null | true
    }

    def "提供占位符的要求"() {
        given:
        ErrMsgEntity3 entity = new ErrMsgEntity3().setName(name)

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            println MkValidators.getErrMsgChain()
            println MkValidators.getErrMsg()
        }
        Assert.assertEquals(result, act)

        where:
        name | result
        "a"  | true
        "b"  | true
        "c"  | true
        "d"  | false
    }

    def "提供占位符关联要求"() {
        given:
        ErrMsgEntity4 entity = new ErrMsgEntity4().setName(name).setAge(12).setAge2(34)

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            println MkValidators.getErrMsgChain()
            println MkValidators.getErrMsg()
        }
        Assert.assertEquals(result, act)

        where:
        name | result
        "a"  | true
        "b"  | true
        "c"  | true
        "d"  | false
    }
}
