package com.simonalong.mikilin.parent

import com.simonalong.mikilin.MkValidators
import com.simonalong.mikilin.range.collection.CollectionSizeEntityA
import com.simonalong.mikilin.range.collection.CollectionSizeEntityB
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2020/3/25 上午2:36
 */
class ParentTest extends Specification {

    /**
     * 针对继承关系，这里只包含继承父类的public部分，和自己的所有属性部分
     */
    def "继承关系"() {
        given:
        ChildEntity entity = new ChildEntity().setSelf(self).setAge1(age1).setAge2(age2).setAge3(age3).setName(name)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        age1 | age2 | age3 | self | name | result
        5    | 20   | 20   | 20   | "a"  | true
        101  | 20   | 20   | 20   | "a"  | true
        5    | 101  | 20   | 20   | "a"  | true
        5    | 20   | 20   | 201  | "a"  | false
        5    | 20   | 201  | 20   | "b"  | false
    }
}
