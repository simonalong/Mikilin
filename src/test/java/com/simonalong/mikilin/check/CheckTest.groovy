package com.simonalong.mikilin.check

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author shizi
 * @since 2021-03-31 16:17:53
 */
class CheckTest extends Specification {

    def "@Check和@Matcher一起核查"() {
        given:
        List<CheckInnerEntity> list = new ArrayList<>();
        list.add(new CheckInnerEntity(age1, name1))
        list.add(new CheckInnerEntity(age2, name2))
        CheckEntity entity = new CheckEntity().setAddress(address).setInnerEntityList(list)

        expect:
        def act = MkValidators.check(entity);
        Assert.assertEquals(result, act)
        if (!act) {
            println MkValidators.errMsgChain
        }

        where:
        address | age1 | name1  | age2 | name2  | result
        "杭州"    | 10   | "chou" | 6    | "song" | true
        "北京"    | 10   | "chou" | 6    | "song" | false
        "杭州"    | 10   | "chou" | 6    | "chou" | false
    }
}
