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
            println JSON.toJSONString(MkValidators.getErrMsgMap())
        }
        Assert.assertEquals(result, act)

        where:
        name | result
        "a"  | true
        "b"  | true
        "c"  | true
        "d"  | false
    }

    def "测试异常map的返回——一层嵌套"() {
        given:
        ErrMsgMapEntity2 entity = new ErrMsgMapEntity2().setName(name).setInnerEntityA(new ErrMsgMapInnerEntityA().setAge(age))

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            println JSON.toJSONString(MkValidators.getErrMsgMap())
        }
        Assert.assertEquals(result, act)

        where:
        name | age | result
        "a"  | 12  | true
        "b"  | 0   | true
        "c"  | 2   | false
        "a"  | 32  | false
    }

    def "测试异常map的返回——两层嵌套"() {
        given:
        ErrMsgMapEntity3 entity = new ErrMsgMapEntity3().setName(name).setInnerEntityB(new ErrMsgMapInnerEntityB().setInnerEntityA(new ErrMsgMapInnerEntityA().setAge(age).setLength(length)))

        expect:
        def act = MkValidators.check(entity)
        if (!act) {
            println JSON.toJSONString(MkValidators.getErrMsgMap())
        }
        Assert.assertEquals(result, act)

        where:
        name | age | length | result
        "a"  | 12  | 111    | true
        "b"  | 0   | 111    | true
        "b"  | 0   | 99     | false
        "b"  | 0   | 121    | false
        "b"  | 13  | 121    | false
        "c"  | 2   | 111    | false
        "a"  | 32  | 111    | false
        "a"  | 11  | 121    | false
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
            println JSON.toJSONString(MkValidators.getErrMsgMap())
        }
        Assert.assertEquals(result, act)

        where:
        name | age1 | length1 | age2 | length2 | result
        "a"  | 12   | 111     | 12   | 111     | true
        "b"  | 0    | 111     | 12   | 111     | true
        "b"  | 0    | 99      | 12   | 111     | false
        "b"  | 0    | 121     | 12   | 111     | false
        "b"  | 13   | 121     | 12   | 111     | false
        "c"  | 2    | 111     | 12   | 111     | false
        "a"  | 32   | 111     | 12   | 111     | false
        "a"  | 11   | 121     | 12   | 111     | false
        "a"  | 11   | 121     | 132  | 111     | false
        "a"  | 11   | 111     | 12   | 321     | false
    }
}
