package com.simon.mikilin.core.match

import com.simon.mikilin.core.Checks
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:08
 */
class FieldJudgeTest extends Specification {

    /**
     * 测试外部判断的调用
     */
    def "外部调用测试"() {
        given:
        JudgeEntity entity = new JudgeEntity().setName(name).setAge(age).setAddress(address)

        expect:
        boolean actResult = Checks.check(entity)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        name    | age  | address    | result
        "women" | 12   | "hangzhou" | false
        "haode" | 13   | "tianjin"  | false
        "b"     | -1   | "tianjin"  | false
        "b"     | 200  | "tianjin"  | false
        "c"     | 12   | "hangzhou" | false
        "c"     | 12   | "beijing"  | false
        "c"     | 12   | "tianjin"  | true
        "d"     | null | "tianjin"  | false
        null    | 32   | "tianjin"  | false
    }
}
