package com.simonalong.mikilin.enumtype

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:29
 */
class EnumTypeTest extends Specification {

    def "枚举类型测试"() {
        given:
        EnumTypeEntity1 judgeEntity = new EnumTypeEntity1(name, tag, invalidTag)

        expect:
        def act = MkValidators.check(judgeEntity)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        name | tag  | invalidTag | result
        "A1" | "A1" | "c"        | true
        "A1" | "B1" | "c"        | true
        "A1" | "B2" | "c"        | true
        "A1" | "B3" | "c"        | true
        "A1" | "A1" | "C1"       | false
        "A1" | "A1" | "C2"       | false
        "A1" | "A1" | "C3"       | false
        "A1" | "A1" | "c"        | true
        "A1" | "A1" | "c"        | true
        "A1" | "A4" | "c"        | false
        "A1" | "A2" | "C4"       | true
        "A1" | "A3" | "C4"       | true
        "A1" | "a"  | "C4"       | false
        "A2" | "A3" | "C1"       | false
        "a"  | "A2" | "C4"       | false
        "c"  | "c"  | "C4"       | false
    }

    def "下标的Integer测试"(){
        given:
        EnumTypeEntity2 entity2 = new EnumTypeEntity2().setName(name)

        expect:
        def act = MkValidators.check(entity2)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        name | result
        0    | true
        1    | true
        2    | true
        3    | true
        4    | false
    }
}
