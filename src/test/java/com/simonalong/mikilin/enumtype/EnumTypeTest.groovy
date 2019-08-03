package com.simonalong.mikilin.enumtype

import com.simonalong.mikilin.Checks
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午9:29
 */
class EnumTypeTest extends Specification {

    def "枚举类型测试"() {
        given:
        JudgeEntity judgeEntity = new JudgeEntity(name, tag, invalidTag)

        expect:
        def act = Checks.check(judgeEntity)
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
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
    }
}
