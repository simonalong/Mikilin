package com.simon.mikilin.core.value

import com.simon.mikilin.core.Checks
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午10:11
 */
class FieldJudgeTest extends Specification {

    def "复杂类型白名单测试"() {
        given:
        WhiteAEntity entity = new WhiteAEntity()
        entity.setName(name as String)

        expect:
        boolean actResult = Checks.check(entity)
        if (!actResult) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        name | result
        "a"  | true
        "b"  | true
        "c"  | true
        null | true
        "d"  | false
    }

    def "复杂类型黑名单测试"() {
        given:
        BlackAEntity entity = new BlackAEntity()
        entity.setName(name as String)

        expect:
        Assert.assertEquals(result, Checks.check(entity))
        if (!result) {
            println Checks.getErrMsg()
        }

        where:
        name | result
        "a"  | false
        "b"  | false
        "c"  | false
        null | false
        "d"  | true
    }

    def "复杂类型黑白名单测试"() {
        given:
        BlackWhiteAEntity entity = new BlackWhiteAEntity()
        entity.setName(name).setAge(age)

        expect:
        Assert.assertEquals(result, Checks.check(entity))
        if (!result) {
            println Checks.getErrMsg()
        }

        where:
        name | age  | result
        "a"  | 3    | true
        "b"  | 4    | true
        "a"  | 1    | false
        "a"  | 2    | false
        "b"  | 1    | false
        "c"  | 3    | false
        "c"  | 4    | false
        "a"  | null | true
        null | 3    | false
        null | null | false
    }

    def "复杂类型白名单复杂结构"() {
        given:
        WhiteBEntity entity = new WhiteBEntity()
        entity.setName(whiteBName).setBEntity(new BEntity().setName(whiteBBName)
                .setAEntity(new AEntity().setName(whiteBAName).setAge(age)))

        Assert.assertEquals(result, Checks.check(entity))
        if (!result) {
            println Checks.getErrMsg()
        }

        expect:
        where:
        whiteBName | whiteBBName | whiteBAName | age | result
        "a"        | "a"         | "a"         | 12  | true
        "a"        | "a"         | "b"         | 12  | true
        "a"        | "b"         | "a"         | 12  | true
        "a"        | "b"         | "b"         | 12  | true
        "a"        | "b"         | "c"         | 12  | true
        "a"        | "b"         | null        | 12  | true
        "a"        | "c"         | "c"         | 12  | false
        "b"        | "c"         | null        | 12  | false
        "b"        | "b"         | "b"         | 12  | true
        "b"        | "b"         | "d"         | 12  | false
        "b"        | null        | "c"         | 12  | false
        null       | "a"         | null        | 12  | false
    }

    def "复杂类型白名单复杂结构2"() {
        given:
        BEntity entity = new BEntity()
        entity.setName(bName).setAEntity(new AEntity().setName(baName).setAge(12))

        expect:
        Assert.assertEquals(result, Checks.check(entity))
        if (!result) {
            println Checks.getErrMsg()
        }

        where:
        bName | baName | result
        "a"   | "b"    | true
        "a"   | "c"    | true
        "a"   | "d"    | false
    }

    /**
     * 测试在复杂结构为空的情况
     */
    def "复杂类型白名单复杂结构3"() {
        given:
        BEntity entity = new BEntity()
        entity.setName(bName)

        expect:
        Assert.assertEquals(result, Checks.check(entity))
        if (!result) {
            println Checks.getErrMsg()
        }

        where:
        bName | result
        "a"   | true
        "b"   | true
        "c"   | false
    }

    def "复杂类型白名单集合复杂结构"() {
        given:
        WhiteCEntity entity = new WhiteCEntity()
        entity.setCEntities(Arrays.asList(new CEntity().setName(ccName)
                .setBEntities(Arrays.asList(new BEntity().setName(cb1Name), new BEntity().setName(cb2Name)))))
                .setBEntity(new BEntity().setName(cName).setAEntity(new AEntity().setName(cbaName).setAge(12)))

        Assert.assertEquals(result, Checks.check(entity))
        if (!result) {
            println Checks.getErrMsg()
        }

        expect:
        where:
        ccName | cb1Name | cb2Name | cName | cbaName | result
        "a"    | "a"     | "a"     | "a"   | "a"     | true
        "a"    | "a"     | "a"     | "a"   | "b"     | true
        "a"    | "a"     | "a"     | "a"   | "c"     | true
        "a"    | "a"     | "b"     | "a"   | "a"     | true
        "b"    | "a"     | "b"     | "a"   | "a"     | true
        "b"    | "c"     | "b"     | "a"   | "a"     | false
        "b"    | "a"     | "b"     | "a"   | null    | true
    }
}
