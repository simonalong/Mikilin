package com.simonalong.mikilin.judge

import com.simonalong.mikilin.Checks
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
        boolean actResult = Checks.check(entity, "name", "age", "address")
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

    /**
     * 测试外部判断的调用
     */
    def "外部调用测试（包含请求实体）"() {
        given:
        JudgeEntity entity = new JudgeEntity().setMRatio(mRatio).setNRatio(nRatio)

        expect:
        boolean actResult = Checks.check(entity, "mRatio")
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        mRatio | nRatio | result
        1f     | 1f     | true
        10f    | 1f     | false
    }

    def "测试上下文两个参数"(){
        given:
        JudgeEntity entity = new JudgeEntity().setTwoPa(twoPa)

        expect:
        boolean actResult = Checks.check(entity, "twoPa")
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        twoPa   | result
        "hello" | true
        "jo"    | false
        "ok"    | false
    }

    def "测试上下文三个参数"() {
        given:
        JudgeEntity entity = new JudgeEntity().setThreePa(threePa)

        expect:
        boolean actResult = Checks.check(entity, "threePa")
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        threePa | result
        "hello" | true
        "word"  | true
        "ok"    | false
        "haode" | false
    }

    def "测试上下文三个参数异常情况"() {
        given:
        JudgeEntity entity = new JudgeEntity().setThreePa(threePa)

        expect:
        boolean actResult = Checks.check(entity, "threePa")
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        threePa | result
        "hello" | true
        "word"  | true
        "ok"    | false
        "haode" | false
    }
}
