package com.simonalong.mikilin.muti

import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.model.IdCardEntity
import org.junit.Assert
import spock.lang.Specification

/**
 * 多匹配器测试用例
 *
 * @author shizi
 * @since 2020/3/24 下午6:25
 */
class MultiMatcherTest extends Specification {

    def "一个匹配器多配器项（或）测试"() {
        given:
        MultiMatcherEntity entity = new MultiMatcherEntity().setCityCode(cityCode).setAge(age).setCode(code)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
            println()
        }
        Assert.assertEquals(result, actResult)

        where:
        cityCode | age | code | result
        "12"     | 5   | 5    | true
        "12"     | 11  | 5    | true
        "13"     | 5   | 5    | false
        "12"     | 120 | 5    | false
        "12"     | 5   | 33   | false
        "12"     | 5   | 15   | false
    }

    def "多个匹配器测试"() {
        given:
        MultiMatcherEntity2 entity = new MultiMatcherEntity2().setCode(code)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!actResult) {
            println MkValidators.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        code | result
        0    | true
        1    | false
        2    | true
        3    | false
        102  | false
    }
}
