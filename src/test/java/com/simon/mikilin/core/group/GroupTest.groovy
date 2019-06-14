package com.simon.mikilin.core.group

import com.simon.mikilin.core.Checks
import com.simon.mikilin.core.enumtype.JudgeEntity
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/6/15 上午12:01
 */
class GroupTest extends Specification{

    // todo 分组测试代码还没搞完，暂时还没有办法进行测试
    def "分组测试"(){
        given:
        JudgeEntity judgeEntity = new JudgeEntity(name, tag, invalidTag);

        expect:
        def act = Checks.check(judgeEntity);
        Assert.assertEquals(result, act)
        if (!act) {
            println Checks.errMsg
        }

        where:
        name | tag  | invalidTag | result
        "A1" | "A1" | "c"        | true
        "A1" | "B1" | "c"        | true
        "A1" | "B2" | "c"        | true
        "A1" | "B3" | "c"        | true
        "A1" | "A1" | "C1"       | false
        "A1" | "A1" | "C2"       | false
        "A1" | "A1" | "C3"       | false
        "A1" | "A1" | "c"        | true
        "A1" | "A1" | "c"        | true
        "A1" | "A4" | "c"        | false
        "A1" | "A2" | "C4"       | true
        "A1" | "A3" | "C4"       | true
        "A1" | "a"  | "C4"       | false
        "A2" | "A3" | "C1"       | false
        "a"  | "A2" | "C4"       | false
    }
}
