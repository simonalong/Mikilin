package com.simonalong.mikilin.parameter

import com.alibaba.fastjson.JSON
import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.exception.MkCheckException
import org.junit.Assert
import spock.lang.Specification

import java.lang.reflect.Method
import java.lang.reflect.Parameter

/**
 * 测试修饰参数
 * @author shizi
 * @since 2021-03-04 21:15:42
 */
class ParameterTest extends Specification {

    def "参数校验：value"() {
        given:
        Method currentMethod = ParameterFunService.class.getMethod("funValue", String.class, Integer.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            Object[] values = [name, age]
            MkValidators.validate(currentMethod, parameters[0], values, 0)
            MkValidators.validate(currentMethod, parameters[1], values, 1)
        } catch (Exception e) {
            ex = e
        }

        then:
        if (null == result) {
            ex instanceof MkCheckException
        } else {
            Assert.assertEquals(null, ex)
        }

        where:
        name   | age | result
        "chen" | 3   | null
        "chen" | 1   | null
        "zhou" | 3   | null
        "zhou" | 1   | 1
    }

    def "参数校验：value——异常map"() {
        given:
        Method currentMethod = ParameterFunService.class.getMethod("funValue", String.class, Integer.class)

        expect:
        Object[] values = [name, age]
        def actResult = MkValidators.check(currentMethod, values)
        if (!actResult) {
            println JSON.toJSONString(MkValidators.errMsgMap)
        }
        Assert.assertEquals(result, actResult)

        where:
        name   | age | result
        "chen" | 3   | false
        "zhou" | 3   | false
        "zhou" | 2   | true
    }

    def "参数校验：groupValue"() {
        given:
        Method currentMethod = ParameterFunService.class.getMethod("funGroupValue", String.class, Integer.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            Object[] values = [name, age]
            MkValidators.validate("g1", currentMethod, parameters[0], values, 0)
            MkValidators.validate("g1", currentMethod, parameters[1], values, 1)
        } catch (Exception e) {
            ex = e
        }

        then:
        if ("error" == result) {
            ex instanceof MkCheckException
        } else {
            Assert.assertEquals(null, ex)
        }

        where:
        name   | age | result
        "chen" | 3   | "error"
        "chen" | 1   | "error"
        "zhou" | 3   | "error"
        "zhou" | 1   | "ok"
    }

    def "参数校验：GroupsValue"() {
        given:
        Method currentMethod = ParameterFunService.class.getMethod("funGroupsValue", String.class, Integer.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            Object[] values = [name, age]
            if (group == "g1") {
                MkValidators.validate(group, currentMethod, parameters[0], values, 0)
                MkValidators.validate(group, currentMethod, parameters[1], values, 1)
            } else {
                MkValidators.validate(group, currentMethod, parameters[1], values, 1)
            }

        } catch (Exception e) {
            ex = e
        }

        then:
        if ("error" == result) {
            ex instanceof MkCheckException
        } else {
            Assert.assertEquals(null, ex)
        }

        where:
        group | name   | age | result
        "g1"  | "chen" | 1   | "error"
        "g1"  | "zhou" | 1   | "ok"
        "g1"  | "zhou" | 3   | "error"
        "g2"  | "chen" | 1   | "ok"
        "g2"  | "huo"  | 1   | "ok"
        "g2"  | "huo"  | 3   | "ok"
    }

    def "参数校验：customize"() {
        given:
        Method currentMethod = ParameterFunService.class.getMethod("funCustomize", Integer.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            Object[] values = [age]
            MkValidators.validate(currentMethod, parameters[0], values, 0)
        } catch (Exception e) {
            ex = e
        }

        then:
        if ("error" == result) {
            ex instanceof MkCheckException
        } else {
            Assert.assertEquals(null, ex)
        }

        where:
        age | result
        13  | "error"
        10  | "ok"
    }
}
