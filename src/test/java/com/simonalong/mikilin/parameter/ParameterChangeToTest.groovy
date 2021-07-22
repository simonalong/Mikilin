package com.simonalong.mikilin.parameter

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * 参数的转换测试
 *
 * @author shizi
 * @since 2021-07-13 21:46:09
 */
class ParameterChangeToTest extends Specification {

    def "参数匹配转换1：value"() {
        given:
        ParameterFunOfChangeToService service = new ParameterFunOfChangeToService()

        when:
        Method currentMethod = service.getClass().getMethod("funValue", String.class, Integer.class)

        Object[] parameterValues = new Object[2]
        parameterValues[0] = name
        parameterValues[1] = age
        MkValidators.check(currentMethod, parameterValues)

        then:
        Assert.assertEquals(result, currentMethod.invoke(service, parameterValues))

        where:
        name | age  | result
        "zhou" |  1 | "_default_:100"
        "song" |  2 | "_default_:100"
        "chen" |  1 | "chen:100"
        "chen" |  3 | "chen:3"
    }

    def "参数匹配转换2：value"() {
        given:
        ParameterFunOfChangeToService service = new ParameterFunOfChangeToService()

        when:
        Method currentMethod = service.getClass().getMethod("funValueBlack", String.class, Integer.class)

        Object[] parameterValues = new Object[2]
        parameterValues[0] = name
        parameterValues[1] = age
        MkValidators.check(currentMethod, parameterValues)

        then:
        Assert.assertEquals(result, currentMethod.invoke(service, parameterValues))

        where:
        name | age  | result
        "zhou" |  1 | "_default_:100"
        "song" |  2 | "_default_:100"
        "chen" |  1 | "chen:100"
        "chen" |  3 | "chen:3"
    }
}
