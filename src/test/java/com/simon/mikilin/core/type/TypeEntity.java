package com.simon.mikilin.core.type;

import com.simon.mikilin.core.annotation.FieldEnum;
import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午3:38
 */
@Data
@Accessors(chain = true)
public class TypeEntity {

    @FieldValidCheck(type = FieldEnum.ID_CARD)
    private String idCardValid;
    @FieldValidCheck(type = FieldEnum.ID_CARD)
    private String idCardInValid;

    @FieldValidCheck(type = FieldEnum.PHONE_NUM, disable = true)
    private String phoneValid;
    @FieldValidCheck(type = FieldEnum.PHONE_NUM, disable = true)
    private String phoneInValid;

    @FieldValidCheck(type = FieldEnum.FIXED_PHONE, disable = true)
    private String fixedPhone;
    @FieldValidCheck(type = FieldEnum.FIXED_PHONE, disable = true)
    private String fixedPhoneInValid;

    @FieldValidCheck(type = FieldEnum.MAIL, disable = true)
    private String mailValid;
    @FieldValidCheck(type = FieldEnum.MAIL, disable = true)
    private String mailInValid;

    @FieldValidCheck(type = FieldEnum.IP_ADDRESS, disable = true)
    private String ipValid;
    @FieldValidCheck(type = FieldEnum.IP_ADDRESS, disable = true)
    private String ipInvalid;


}
