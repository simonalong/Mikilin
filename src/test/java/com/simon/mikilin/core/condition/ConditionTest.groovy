package com.simon.mikilin.core.condition

import com.simon.mikilin.core.Checks
import com.simon.mikilin.core.enumtype.JudgeEntity
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/4/14 下午12:05
 */
@SuppressWarnings("all")
class ConditionTest extends Specification {

    def "测试基本表达式"() {
        given:
        ConditionEntity1 entity = new ConditionEntity1().setNum1(num1).setNum2(num2).setNum3(num3)

        expect:
        def act = Checks.check(entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
        }

        where:
        num1 | num2 | num3 | result
        91   | 10   | 31   | true
        90   | 10   | 31   | false
        81   | 20   | 31   | false
        91   | 10   | 30   | false
    }

    def "测试java表达式"() {
        given:
        ConditionEntity2 entity = new ConditionEntity2().setAge(age).setJudge(judge)

        expect:
        def act = Checks.check(entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
        }

        where:
        age | judge | result
        12  | true  | true
        12  | false | false
    }

    def "测试java数学函数表达式"() {
        given:
        ConditionEntity3 entity = new ConditionEntity3().setNum1(num1).setNum2(num2).setNum3(num3)

        expect:
        def act = Checks.check(entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
        }

        where:
        num1 | num2 | num3 | result
        31   | 30   | 29   | true
        31   | 30   | 30   | false
        31   | 30   | 20   | true
        31   | 30   | 31   | false
    }
}
