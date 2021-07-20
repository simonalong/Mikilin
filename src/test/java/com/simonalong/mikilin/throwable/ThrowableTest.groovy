package com.simonalong.mikilin.throwable

import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.exception.MkCheckException
import com.simonalong.mikilin.exception.MkException
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong* @since 2019/3/10 下午10:16
 */
class ThrowableTest extends Specification {

    def "正则表达式测试"() {
        given:
        ThrowableEntity entity = new ThrowableEntity().setName(name)

        when:
        Exception ex = null
        try {
            MkValidators.check(entity)
        } catch (Exception e) {
            ex = e
        }

        then:
        if (null == result) {
            ex instanceof TestException
        } else {
            Assert.assertEquals(null, ex)
        }

        where:
        name | result
        null | null
        "ok" | 1
    }
}
