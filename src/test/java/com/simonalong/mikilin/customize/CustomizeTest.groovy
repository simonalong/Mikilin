package com.simonalong.mikilin.customize

import com.alibaba.fastjson.JSON
import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:08
 */
class CustomizeTest extends Specification {

    /**
     * 测试外部判断的调用
     */
    def "外部调用测试"() {
        given:
        CustomizeEntity entity = new CustomizeEntity().setName(name).setAge(age).setAddress(address)

        expect:
        boolean actResult = MkValidators.check(entity, "name", "age", "address")
        if (!actResult) {
            println MkValidators.getErrMsgChain()
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
        CustomizeEntity entity = new CustomizeEntity().setMRatio(mRatio).setNRatio(nRatio)

        expect:
        boolean actResult = MkValidators.check(entity, "mRatio")
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        mRatio | nRatio | result
        1f     | 1f     | true
        10f    | 1f     | false
    }

    def "测试上下文两个参数"(){
        given:
        CustomizeEntity entity = new CustomizeEntity().setTwoPa(twoPa)

        expect:
        boolean actResult = MkValidators.check(entity, "twoPa")
        if (!actResult) {
            println MkValidators.getErrMsgChain()
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
        CustomizeEntity entity = new CustomizeEntity().setThreePa(threePa)

        expect:
        boolean actResult = MkValidators.check(entity, "threePa")
        if (!actResult) {
            println MkValidators.getErrMsgChain()
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
        CustomizeEntity entity = new CustomizeEntity().setThreePa(threePa)

        expect:
        boolean actResult = MkValidators.check(entity, "threePa")
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        threePa | result
        "hello" | true
        "word"  | true
        "ok"    | false
        "haode" | false
    }

    def "测试上下文三个参数异常情况2"() {
        given:
        CustomizeEntity entity = new CustomizeEntity().setThreePa2(threePa)

        expect:
        boolean actResult = MkValidators.check(entity, "threePa2")
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        threePa | result
        "hello" | true
        "word"  | true
        "ok"    | false
        "haode" | false
    }

    def "不匹配情况下的错误日志"() {
        given:
        CustomizeEntity entity = new CustomizeEntity().setFieldErrMsg(fieldErrMsg)

        expect:
        boolean actResult = MkValidators.check(entity, "fieldErrMsg")
        if (!actResult) {
            println MkValidators.getErrMsgChain()
            println MkValidators.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        fieldErrMsg | result
        "mock1"     | true
        "mock2"     | true
        "mock3"     | true
        "asdf"      | false
    }

    def "不匹配情况下的错误日志2"() {
        given:
        CustomizeEntity entity = new CustomizeEntity().setFieldErrMsg2(fieldErrMsg)

        expect:
        boolean actResult = MkValidators.check(entity, "fieldErrMsg2")
        if (!actResult) {
            println MkValidators.getErrMsgChain()
            println MkValidators.getErrMsg()
            Assert.assertEquals(errMsg, JSON.toJSONString(MkValidators.getErrMsgMap()))
        }
        Assert.assertEquals(result, actResult)

        where:
        fieldErrMsg | result | errMsg
        "mock1"     | true | ""
        "mock2"     | true | ""
        "mock3"     | true | ""
        "asdf"      | false | "{\"fieldErrMsg2\":\"当前的值不符合需求\"}"
    }
}
