package com.simon.mikilin.core.judge

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
        JudgeEntity entity = new JudgeEntity().setName(name).setAge(age)

        expect:
        boolean actResult = Checks.check(entity)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        name    | age  || result
        "women" | 12   
        "haode" | 13   
        "b"     | -1   
        "b"     | 200  
        "c"     | 12   || true
        "d"     | null 
        null    | 32   
    }
}
