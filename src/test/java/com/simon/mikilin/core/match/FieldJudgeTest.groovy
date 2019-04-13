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
    // todo 有问题
    def "外部调用测试"() {
        given:
        JudgeEntity entity = new JudgeEntity().setName(name).setAge(age)

        expect:
        boolean actResult = Checks.check(entity)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        name    | age  | result
        "women" | 12   | false
//        "haode" | 13   | false
//        "b"     | -1   | false
//        "b"     | 200  | false
//        "c"     | 12   | true
//        "d"     | null | false
//        null    | 32   | false
    }
}
