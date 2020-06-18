package com.simonalong.mikilin.object

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2020/3/25 上午12:28
 */
class ObjectTest extends Specification {

    def "基本类型测试"() {
        given:
        ObjectEntity range = new ObjectEntity().setName(name)

        expect:
        boolean actResult = MkValidators.check(range)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name | result
        "a"  | true
        "b"  | true
        "c"  | false
        null  | false
    }

    def "集合类型测试"() {
        given:
        List<ObjectEntity> entityList = new ArrayList<>();
        entityList.add(new ObjectEntity().setName(name1));
        entityList.add(new ObjectEntity().setName(name2));

        expect:
        boolean actResult = MkValidators.check(entityList)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name1 | name2 | result
        "a"   | "a"   | true
        "a"   | "b"   | true
        "a"   | "c"   | false
        "a"   | "d"   | false
        "d"   | "c"   | false
    }

    def "map类型测试"() {
        given:
        Map<String, ObjectEntity> entityMap = new HashMap<>()
        entityMap.put("a", new ObjectEntity().setName(name1))
        entityMap.put("b", new ObjectEntity().setName(name2))

        expect:
        boolean actResult = MkValidators.check(entityMap)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name1 | name2 | result
        "a"   | "a"   | true
        "a"   | "b"   | true
        "a"   | "c"   | false
        "a"   | "d"   | false
        "d"   | "c"   | false
    }

    def "数组类型测试"() {
        given:
        ObjectEntity[] entities = new ObjectEntity[2]
        entities[0]=new ObjectEntity().setName(name1);
        entities[1]=new ObjectEntity().setName(name2);

        expect:
        boolean actResult = MkValidators.check(entities)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name1 | name2 | result
        "a"   | "a"   | true
        "a"   | "b"   | true
        "a"   | "c"   | false
        "a"   | "d"   | false
        "d"   | "c"   | false
    }

    def "多维数组类型测试"() {
        given:
        ObjectEntity[][] entities = new ObjectEntity[2][2]
        entities[0][0]=new ObjectEntity().setName(name1);
        entities[0][1]=new ObjectEntity().setName(name2);

        expect:
        boolean actResult = MkValidators.check(entities)
        if (!actResult) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        name1 | name2 | result
        "a"   | "a"   | true
        "a"   | "b"   | true
        "a"   | "c"   | false
        "a"   | "d"   | false
        "d"   | "c"   | false
    }
}
