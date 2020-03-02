package com.simonalong.mikilin.group

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/6/15 上午12:01
 */
@SuppressWarnings("all")
class GroupTest extends Specification {

    def "测试默认分组"() {
        given:
        GroupEntity entity = new GroupEntity().setAge(age).setName(name)

        expect:
        def act = MkValidators.check(entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        age | name       | result
        12  | "shanghai" | true
        12  | "beijing"  | true
        49  | "beijing"  | true
        50  | "beijing"  | false
        100 | "beijing"  | false
        49  | "tianjin"  | false
    }

    def "测试指定分组"() {
        given:
        GroupEntity entity = new GroupEntity().setAge(age).setName(name)

        expect:
        def act = MkValidators.check("test1", entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        age | name        | result
        10  | "shanghai"  | true
        12  | "beijing"   | false
        23  | "beijing"   | false
        50  | "beijing"   | true
        100 | "guangzhou" | false
    }

    def "测试指定分组指定属性"() {
        given:
        GroupEntity entity = new GroupEntity().setAge(age).setName(name)

        expect:
        def act = MkValidators.check("test1", entity, "age");
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        age | name        | result
        10  | "shanghai"  | true
        12  | "beijing"   | false
        23  | "beijing"   | false
        50  | "beijing"   | true
        100 | "guangzhou" | true
    }

    def "分组多个组合_测试default"() {
        given:
        GroupMultiEntity entity = new GroupMultiEntity().setAge(age).setName(name)

        expect:
        def act = MkValidators.check(entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        age | name       | result
        20  | "shanghai" | false
        25  | "beijing"  | false
        30  | "beijing"  | false
        30  | "shanghai" | false
        40  | "hangzhou" | true
    }

    /**
     * 其中test1在组的name中不存在，则就不进行核查，只核查age
     * @return
     */
    def "分组多个组合_测试组test1"() {
        given:
        GroupMultiEntity entity = new GroupMultiEntity().setAge(age).setName(name)

        expect:
        def act = MkValidators.check("test1", entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        age | name        | result
        10  | "hangzhou"  | true
        20  | "hangzhou"  | false
        25  | "hangzhou"  | false
        30  | "shanghai"  | true
        30  | "zhengzhou" | true
    }

    /**
     * 测试组在两个属性中都存在
     * @return
     */
    def "分组多个组合_测试组test2"() {
        given:
        GroupMultiEntity entity = new GroupMultiEntity().setAge(age).setName(name)

        expect:
        def act = MkValidators.check("test2", entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        age | name        | result
        10  | "beijing"   | true
        20  | "beijing"   | false
        25  | "beijing"   | false
        30  | "beijing"   | true
        40  | "beijing"   | true
        40  | "hangzhou"  | false
        40  | "zhengzhou" | false
        40  | "shanghai"  | true
    }

    /**
     * 测试组在两个属性中都存在
     * @return
     */
    def "分组多个组合_测试组test0"() {
        given:
        GroupMultiEntity entity = new GroupMultiEntity().setAge(age).setName(name)

        expect:
        def act = MkValidators.check("test0", entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        age | name        | result
        20  | "beijing"   | true
        25  | "beijing"   | true
        70  | "beijing"   | false
        79  | "hangzhou"  | false
        80  | "zhengzhou" | true
        90  | "shanghai"  | true
    }
}
