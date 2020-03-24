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

    def "多匹配器测试1"() {
        given:
        MultiMatcherEntity entity = new MultiMatcherEntity().setName(name).setCode(code)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name                  | code | result
        "b"                   | 12   | true
        "a"                   | 12   | true
        "c"                   | 12   | false
        "410928199102226311"  | 12   | true
        "4211982119910725985" | 12   | false
        "410928199107259855"  | 101  | false
    }
}
