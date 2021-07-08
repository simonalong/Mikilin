package com.simonalong.mikilin.changeto

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi* @since 2021-03-31 23:54:23
 */
class ChangeToTest extends Specification {

    def "测试属性匹配之后的转换1"() {
        given:
        ChangeEntity entity = new ChangeEntity().setAge(age)

        expect:
        MkValidators.check(entity, "age");
        Assert.assertEquals(ageAfter, entity.getAge())

        where:
        age | ageAfter
        12  | 30
        20  | 30
        25  | 25
    }

    def "测试属性匹配之后的转换2"() {
        given:
        ChangeEntity entity = new ChangeEntity().setName(name)

        expect:
        MkValidators.check(entity, "name");
        Assert.assertEquals(ageAfter, entity.getName())

        where:
        name    | ageAfter
        ""      | "_default_"
        null    | "_default_"
        "hello" | "hello"
    }
}
