package com.simonalong.mikilin.parameter

import com.alibaba.fastjson.JSON
import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.exception.MkCheckException
import lombok.extern.slf4j.Slf4j
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
        Method currentMethod = ParameterEntity.class.getMethod("funValue", String.class, Integer.class)
        Parameter[] parameters = currentMethod.getParameters();

        when:
        Exception ex = null
        try {
            MkValidators.validate(currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        value  | index
        "chen" | 0
        3      | 1
    }

    def "参数校验：value——异常map"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funValue", String.class, Integer.class)

        expect:
        Object[] values = ["chen", 3]
        def actResult = MkValidators.check(currentMethod, values)
        if (!actResult) {
            println JSON.toJSONString(MkValidators.errMsgMap)
        }
        Assert.assertEquals(result, actResult)

        where:
        value  | index | result
        "chen" | 0     | false
        3      | 1     | false
    }

    def "参数校验：groupValue"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funGroupValue", String.class, Integer.class)
        Parameter[] parameters = currentMethod.getParameters();

        when:
        Exception ex = null
        try {
            MkValidators.validate("g1", currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        value  | index
        "chen" | 0
        3      | 1
    }

    def "参数校验：GroupsValue"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funGroupsValue", String.class, Integer.class)
        Parameter[] parameters = currentMethod.getParameters();

        when:
        Exception ex = null
        try {
            MkValidators.validate(group, currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        group | value  | index
        "g1"  | "chen" | 0
        "g1"  | 3      | 1
        "g2"  | "zhou" | 0
    }

    def "参数校验：NotNull"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funNotNull", String.class, Integer.class)
        Parameter[] parameters = currentMethod.getParameters();

        when:
        Exception ex = null
        try {
            MkValidators.validate(currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        value | index
        null  | 0
        null  | 1
    }

    def "参数校验：NotBlank"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funNotBlank", String.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            MkValidators.validate(currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        value | index
        null  | 0
        ""    | 0
        ''    | 0
    }

    def "参数校验：Model"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funModel", String.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            MkValidators.validate(currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        value        | index
        157098043716 | 0
    }

    def "参数校验：range"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funRange", String.class, Integer.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            MkValidators.validate(currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        value   | index
        ""      | 0
        "a"     | 0
        "abcde" | 0
        8       | 1
        9       | 1
        21      | 1
    }

    def "参数校验：condition"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funCondition", Integer.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            MkValidators.validate(currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        value | index
        13    | 0
        14    | 0
        21    | 0
    }

    def "参数校验：regex"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funRegex", String.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            MkValidators.validate(currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        value   | index
        "1.2."  | 0
        "1.b.d" | 0
        "1.x.3" | 0
    }

    def "参数校验：customize"() {
        given:
        Method currentMethod = ParameterEntity.class.getMethod("funCustomize", Integer.class)
        Parameter[] parameters = currentMethod.getParameters()

        when:
        Exception ex = null
        try {
            MkValidators.validate(currentMethod, parameters[index], value)
        } catch (Exception e) {
            ex = e
        }

        then:
        ex instanceof MkCheckException

        where:
        value | index
        13    | 0
        14    | 0
        21    | 0
    }
}
