package com.github.simonalong.mikilin.common

import com.github.simonalong.mikilin.Checks
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/6/12 下午10:03
 */
class CheckTest extends Specification {

    def "测试指定的属性name"() {
        given:
        TestEntity entity = new TestEntity().setName(name).setAge(age)

        expect:
        def act = Checks.check(entity, "name");
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
        }

        where:
        name     | age | result
        "nihao"  | 12  | false
        "ok"     | 32  | false
        "hehe"   | 20  | true
        "haohao" | 40  | true
    }

    def "测试指定的属性age"() {
        given:
        TestEntity entity = new TestEntity().setName(name).setAge(age)

        expect:
        def act = Checks.check(entity, "age");
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
        }

        where:
        name     | age | result
        "nihao"  | 12  | true
        "ok"     | 32  | true
        "hehe"   | 20  | true
        "haohao" | 40  | false
    }

    def "测试指定的属性address"() {
        given:
        TestEntity entity = new TestEntity().setName(name).setAge(age).setAddress(address)

        expect:
        def act = Checks.check(entity, "address");
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
        }

        where:
        name     | age | address | result
        "nihao"  | 12  | "beijing" | true
        "ok"     | 32  | "shanghai" | true
        "hehe"   | 20  | "hangzhou" | false
        "haohao" | 40  | "zhengzhou" | false
    }
}
