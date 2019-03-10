package com.simon.mikilin.core.type

import com.simon.mikilin.core.Checks
import com.simon.mikilin.core.annotation.FieldEnum
import com.simon.mikilin.core.annotation.FieldValidCheck
import com.simon.mikilin.core.judge.JudgeEntity
import org.junit.Assert
import spock.lang.Specification

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午3:54
 */
class TypeTest extends Specification {

    @FieldValidCheck(type = FieldEnum.ID_CARD)
    private String idCardValid;
    @FieldValidCheck(type = FieldEnum.ID_CARD)
    private String idCardInValid;

    @FieldValidCheck(type = FieldEnum.PHONE_NUM)
    private String phoneValid;
    @FieldValidCheck(type = FieldEnum.PHONE_NUM)
    private String phoneInValid;

    @FieldValidCheck(type = FieldEnum.FIXED_PHONE)
    private String fixedPhone;
    @FieldValidCheck(type = FieldEnum.FIXED_PHONE)
    private String fixedPhoneInValid;

    @FieldValidCheck(type = FieldEnum.MAIL)
    private String mailValid;
    @FieldValidCheck(type = FieldEnum.MAIL)
    private String mailInValid;

    @FieldValidCheck(type = FieldEnum.IP_ADDRESS)
    private String ipValid;
    @FieldValidCheck(type = FieldEnum.IP_ADDRESS)
    private String ipInvalid;

    def "type测试"() {
        given:
        TypeEntity entity = new TypeEntity().setIdCardValid(idCardValid).setIdCardInValid(idCardInvalid)

        expect:
        boolean actResult = Checks.check(entity)
        if (!result) {
            println Checks.getErrMsg()
        }
        Assert.assertEquals(result, actResult)

        where:
        idCardValid          | idCardInvalid        | result
        "28712381"           | "28712381dfsd"       | false
        "411928199102226311" | "411928199102226311" | true
    }
}
