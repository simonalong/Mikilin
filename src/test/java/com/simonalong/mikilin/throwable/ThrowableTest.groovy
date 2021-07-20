package com.simonalong.mikilin.throwable

import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.exception.MkCheckException
import com.simonalong.mikilin.exception.MkException
import com.simonalong.mikilin.parameter.ParameterFunOfChangeToService
import org.junit.Assert
import spock.lang.Specification

import java.lang.reflect.Method

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


    def "异常的匹配转换"() {
        given:
        ParameterFunOfThrowableService service = new ParameterFunOfThrowableService()

        when:
        Exception ex = null
        Method currentMethod = service.getClass().getMethod("funValue", String.class)

        Object[] parameterValues = new Object[1]
        parameterValues[0] = name
        try {
            MkValidators.check(currentMethod, parameterValues)
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
        name   | result
        "zhou" | null
        "song" | null
        "chen" | "ok"
    }
}
