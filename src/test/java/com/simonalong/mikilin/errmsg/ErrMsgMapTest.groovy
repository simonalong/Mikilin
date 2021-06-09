package com.simonalong.mikilin.errmsg

import com.alibaba.fastjson.JSON
import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2021-06-02 19:39:13
 */
class ErrMsgMapTest extends Specification {

    def "测试异常map的返回——无嵌套"() {
        given:
        ErrMsgMapEntity1 entity = new ErrMsgMapEntity1().setName(name).setAge(12).setAge2(34)

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            Assert.assertEquals(errMsg, JSON.toJSONString(MkValidators.getErrMsgMap()))
        }
        Assert.assertEquals(result, act)

        where:
        name | result | errMsg
        "a"  | true   | ""
        "b"  | true   | ""
        "c"  | true   | ""
        "d"  | false  | "{\"name\":\"值d不符合要求, 值age=12，值age2=122\"}"
    }

    def "测试异常map的返回——一层嵌套"() {
        given:
        ErrMsgMapEntity2 entity = new ErrMsgMapEntity2().setName(name).setInnerEntityA(new ErrMsgMapInnerEntityA().setAge(age))

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            Assert.assertEquals(errMsg, JSON.toJSONString(MkValidators.getErrMsgMap()))
        }
        Assert.assertEquals(result, act)

        where:
        name | age | result | errMsg
        "a"  | 12  | true   | ""
        "b"  | 0   | true   | ""
        "c"  | 2   | false  | "{\"name\":\"属性 name 的值 c 不在只可用列表 [a, b] 中\"}"
        "a"  | 32  | false  | "{\"innerEntityA\":{\"age\":\"属性 age 的 值 32 没有命中只允许的范围 [0, 12]\"}}"
    }

    def "测试异常map的返回——两层嵌套"() {
        given:
        ErrMsgMapEntity3 entity = new ErrMsgMapEntity3().setName(name).setInnerEntityB(new ErrMsgMapInnerEntityB().setInnerEntityA(new ErrMsgMapInnerEntityA().setAge(age).setLength(length)))

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            Assert.assertEquals(errMsg, JSON.toJSONString(MkValidators.getErrMsgMap()))
        }
        Assert.assertEquals(result, act)

        where:
        name | age | length | result | errMsg
        "a"  | 12  | 111    | true   | ""
        "b"  | 0   | 111    | true   | ""
        "b"  | 0   | 99     | false  | "{\"innerEntityB\":{\"innerEntityA\":{\"length\":\"属性 length 的 值 99 没有命中只允许的范围 [100, 120]\"}}}"
        "b"  | 0   | 121    | false  | "{\"innerEntityB\":{\"innerEntityA\":{\"length\":\"属性 length 的 值 121 没有命中只允许的范围 [100, 120]\"}}}"
        "b"  | 13  | 121    | false  | "{\"innerEntityB\":{\"innerEntityA\":{\"age\":\"属性 age 的 值 13 没有命中只允许的范围 [0, 12]\"}}}"
        "c"  | 2   | 111    | false  | "{\"name\":\"属性 name 的值 c 不在只可用列表 [a, b] 中\"}"
        "a"  | 32  | 111    | false  | "{\"innerEntityB\":{\"innerEntityA\":{\"age\":\"属性 age 的 值 32 没有命中只允许的范围 [0, 12]\"}}}"
        "a"  | 11  | 121    | false  | "{\"innerEntityB\":{\"innerEntityA\":{\"length\":\"属性 length 的 值 121 没有命中只允许的范围 [100, 120]\"}}}"
    }

    def "测试异常map的返回——多层泛型结构"() {
        given:
        ErrMsgMapEntity4 entity = new ErrMsgMapEntity4().setName(name);
        List<ErrMsgMapInnerEntityB> errMsgMapInnerEntityBList = new ArrayList<>();
        errMsgMapInnerEntityBList.add(new ErrMsgMapInnerEntityB().setInnerEntityA(new ErrMsgMapInnerEntityA().setAge(age1).setLength(length1)))
        errMsgMapInnerEntityBList.add(new ErrMsgMapInnerEntityB().setInnerEntityA(new ErrMsgMapInnerEntityA().setAge(age2).setLength(length2)))

        entity.setInnerEntityBList(errMsgMapInnerEntityBList);

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            Assert.assertEquals(errMsg, JSON.toJSONString(MkValidators.getErrMsgMap()))
        }
        Assert.assertEquals(result, act)

        where:
        name | age1 | length1 | age2 | length2 | result | errMsg
        "a"  | 12   | 111     | 12   | 111     | true   | ""
        "b"  | 0    | 111     | 12   | 111     | true   | ""
        "b"  | 0    | 99      | 12   | 111     | false  | "{\"innerEntityBList\":{\"innerEntityA\":{\"length\":\"属性 length 的 值 99 没有命中只允许的范围 [100, 120]\"}}}"
        "b"  | 0    | 121     | 12   | 111     | false  | "{\"innerEntityBList\":{\"innerEntityA\":{\"length\":\"属性 length 的 值 121 没有命中只允许的范围 [100, 120]\"}}}"
        "b"  | 13   | 121     | 12   | 111     | false  | "{\"innerEntityBList\":{\"innerEntityA\":{\"age\":\"属性 age 的 值 13 没有命中只允许的范围 [0, 12]\"}}}"
        "c"  | 2    | 111     | 12   | 111     | false  | "{\"name\":\"属性 name 的值 c 不在只可用列表 [a, b] 中\"}"
        "a"  | 32   | 111     | 12   | 111     | false  | "{\"innerEntityBList\":{\"innerEntityA\":{\"age\":\"属性 age 的 值 32 没有命中只允许的范围 [0, 12]\"}}}"
        "a"  | 11   | 121     | 12   | 111     | false  | "{\"innerEntityBList\":{\"innerEntityA\":{\"length\":\"属性 length 的 值 121 没有命中只允许的范围 [100, 120]\"}}}"
        "a"  | 11   | 121     | 132  | 111     | false  | "{\"innerEntityBList\":{\"innerEntityA\":{\"length\":\"属性 length 的 值 121 没有命中只允许的范围 [100, 120]\"}}}"
        "a"  | 11   | 111     | 12   | 321     | false  | "{\"innerEntityBList\":{\"innerEntityA\":{\"length\":\"属性 length 的 值 321 没有命中只允许的范围 [100, 120]\"}}}"
    }

    def "测试异常map的返回——无嵌套——自定义返回"() {
        given:
        ErrMsgMapEntity5 entity = new ErrMsgMapEntity5().setAge(age)

        expect:
        def act = MkValidators.check(entity, "age")
        if (!act) {
            Assert.assertEquals(errMsg, JSON.toJSONString(MkValidators.getErrMsgMap()))
        }
        Assert.assertEquals(result, act)

        where:
        age | result | errMsg
        10  | true   | ""
        20  | false  | "{\"age\":\"值20不符合要求\"}"
        30  | true   | ""
        40  | true   | ""
    }

    def "测试异常map的返回——无嵌套——自定义返回2"() {
        given:
        ErrMsgMapEntity5 entity = new ErrMsgMapEntity5().setAge2(age)

        expect:
        def act = MkValidators.check(entity, "age2")
        if (!act) {
            Assert.assertEquals(errMsg, JSON.toJSONString(MkValidators.getErrMsgMap()))
        }
        Assert.assertEquals(result, act)

        where:
        age | result | errMsg
        10  | true   | ""
        20  | false  | "{\"age2\":\"属性 age2 的 值 20 没有命中只允许的范围 [0, 12]\"}"
        30  | true   | ""
        40  | true   | ""
    }
}
