package com.simonalong.mikilin.condition

import com.simonalong.mikilin.MkValidators
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
        def act = MkValidators.check(entity)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
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
        def act = MkValidators.check(entity)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
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
        def act = MkValidators.check(entity)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        num1 | num2 | num3 | result
        31   | 30   | 29   | true
        31   | 30   | 30   | false
        31   | 30   | 20   | true
        31   | 30   | 31   | false
    }

    def "测试复杂逻辑"() {
        given:
        ConditionEntity4 entity = new ConditionEntity4().setType(type).setName(name)

        expect:
        def act = MkValidators.check(entity)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        type | name | result
        0    | "a"  | true
        0    | "b"  | true
        1    | "b"  | true
        2    | "b"  | true
        2    | "c"  | false
        3    | "b"  | false
    }


    def "集合的配置处理"() {
        given:
        def nameList
        if(null != names){
            nameList = Arrays.asList(names.split(","))
        }
        ConditionEntity5 entity = new ConditionEntity5().setHandleType(type).setNameList(nameList)

        expect:
        def act = MkValidators.check(entity)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsg
            println MkValidators.errMsgChain
        }

        where:
        type | names   | result
        0    | null    | true
        0    | "a,b,c" | false
        1    | "a,b,c" | true
        1    | null    | false
    }

    def "至少一个为非空"() {
        given:
        ConditionEntity6 entity = new ConditionEntity6().setF1(f1).setF2(f2).setF3(f3)

        expect:
        def act = MkValidators.check(entity)
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsg
            println MkValidators.errMsgChain
        }

        where:
        f1   | f2   | f3   | result
        1L   | "a"  | 2    | true
        null | "a"  | 2    | true
        null | null | 2    | true
        null | null | null | false
    }
}
