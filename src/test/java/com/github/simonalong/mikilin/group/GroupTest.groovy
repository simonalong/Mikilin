package com.github.simonalong.mikilin.group

import com.github.simonalong.mikilin.Checks
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
        def act = Checks.check(entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
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
        def act = Checks.check("test1", entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
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
        def act = Checks.check("test1", entity, "age");
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
        }

        where:
        age | name        | result
        10  | "shanghai"  | true
        12  | "beijing"   | false
        23  | "beijing"   | false
        50  | "beijing"   | true
        100 | "guangzhou" | true
    }
}
