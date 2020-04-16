package com.simonalong.mikilin.type

import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.exception.MkException
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/8/15 下午10:49
 */
class TypeTest extends Specification {

    def "测试基本类型"() {
        given:
        TypeEntity entity = new TypeEntity().setData(intData)

        expect:
        boolean actResult = MkValidators.check(entity, "data")
        if (!result) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        intData | result
        12      | true
    }

    def "测试明写继承关系类型"() {
        given:
        TypeEntity entity = new TypeEntity().setName(name)

        expect:
        boolean actResult = MkValidators.check(entity, "name")
        if (!result) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name   | result
        'asd'  | true
        "abad" | true
    }

    def "测试不明写类继承关系1"() {
        given:
        TypeEntity entity = new TypeEntity().setObj(obj)

        expect:
        boolean actResult = MkValidators.check(entity, "obj")
        if (!result) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        obj    | result
        'c'    | false
        "abad" | false
        1232   | true
        1232l  | false
        1232f  | true
        12.0f  | true
        -12    | true
    }

    /**
     * 可以让一切数字类型通过
     * @return
     */
    def "测试不明写类继承关系2"() {
        given:
        TypeEntity entity = new TypeEntity().setNum(obj)

        expect:
        boolean actResult = MkValidators.check(entity, "num")
        if (!result) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        obj    | result
        'c'    | false
        "abad" | false
        1232   | true
        1232l  | true   // 这里跟上面的例子的区别
        1232f  | true
        12.0f  | true
        -12    | true
    }

//    def "测试修饰类型不匹配情况"() {
//        given:
//        TypeEntity entity = new TypeEntity().setErrStr(1)
//
//        when:
//        MkValidators.check(entity, "errStr")
//
//        then:
//        thrown(MkException)
//    }
}
