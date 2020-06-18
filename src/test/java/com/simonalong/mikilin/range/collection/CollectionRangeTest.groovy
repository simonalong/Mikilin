package com.simonalong.mikilin.range.collection

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong* @since 2019-09-15 21:29
 */
class CollectionRangeTest extends Specification {

    def "集合类型的启动"() {
        given:
        CollectionSizeEntityB b1 = new CollectionSizeEntityB().setBSize(b11)
        CollectionSizeEntityB b2 = new CollectionSizeEntityB().setBSize(b12)
        List<CollectionSizeEntityB> bList = Arrays.asList(b1, b2);
        CollectionSizeEntityA range = new CollectionSizeEntityA().setBList(bList)

        expect:
        boolean actResult = MkValidators.check(range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        b11 | b12 | result
        5   | 20  | false
        10  | 20  | false
        20  | 20  | true
        30  | 20  | false
        null  | 20  | false
    }

    def "集合类型的启动2"() {
        given:
        CollectionSizeEntityB b1 = new CollectionSizeEntityB().setBSize(b11)
        CollectionSizeEntityB b2 = new CollectionSizeEntityB().setBSize(b12)
        CollectionSizeEntityB b3 = new CollectionSizeEntityB().setBSize(b13)
        List<CollectionSizeEntityB> bList = Arrays.asList(b1, b2, b3);
        CollectionSizeEntityA range = new CollectionSizeEntityA().setBList(bList)

        expect:
        boolean actResult = MkValidators.check(range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        b11 | b12 | b13 | result
        5   | 20  | 20  | false
        10  | 20  | 20  | false
        20  | 20  | 20  | false
        30  | 20  | 20  | false
    }
}
