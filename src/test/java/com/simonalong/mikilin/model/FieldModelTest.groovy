package com.simonalong.mikilin.model

import com.simonalong.mikilin.MkValidators
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午3:54
 */
class FieldModelTest extends Specification {

    def "身份证测试"() {
        given:
        IdCardEntity entity = new IdCardEntity().setIdCardValid(valid).setIdCardInValid(invalid)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!actResult) {
            println MkValidators.getErrMsg()
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        valid                | invalid              | result
        "28712381"           | "411928199102226311" | false
        "28712381"           | "28712381"           | false
        "411928199102226311" | "28712381"           | true
        "411928199102226311" | "411928199102226311" | false
        null | "411928199102226311" | false
    }

    def "手机号测试"() {
        given:
        PhoneEntity entity = new PhoneEntity().setPhoneValid(valid).setPhoneInValid(invalid)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!result) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        valid         | invalid       | result
        "1387772"     | "15700092345" | false
        "28712381"    | "28712381"    | false
        "15700092345" | "28712381"    | true
        "15700092345" | "15700092345" | false
        null | "15700092345" | false
    }

    def "固定电话测试"() {
        given:
        FixPhoneEntity entity = new FixPhoneEntity().setFixedPhone(valid).setFixedPhoneInValid(invalid)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!result) {
            println MkValidators.getErrMsgChain()
            println MkValidators.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        valid          | invalid        | result
        "1387772"      | "0393-3879765" | false
        "28712381"     | "28712381"     | false
        "0393-3879765" | "28712381"     | true
        "0393-3879765" | "0393-3879765" | false
    }

    def "邮箱测试"() {
        given:
        MailEntity entity = new MailEntity().setMailValid(valid).setMailInValid(invalid)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!result) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        valid            | invalid          | result
        "123@"           | "123lan@163.com" | false
        "123@"           | "123@"           | false
        "123lan@163.com" | "123@"           | true
        "123lan@163.com" | "123lan@163.com" | false
    }

    def "IP测试"() {
        given:
        IpEntity entity = new IpEntity().setIpValid(valid).setIpInvalid(invalid)

        expect:
        boolean actResult = MkValidators.check(entity)
        if (!result) {
            println MkValidators.getErrMsgChain()
        }
        Assert.assertEquals(result, actResult)

        where:
        valid             | invalid           | result
        "192.231asdf"     | "192.123.231.222" | false
        "192.231asdf"     | "192.231asdf"     | false
        "192.123.231.222" | "192.231asdf"     | true
        "192.123.231.222" | "192.123.231.222" | false
    }
}
