package com.simonalong.mikilin.check;

import com.simonalong.mikilin.annotation.Check;
import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author shizi
 * @since 2021-03-31 16:17:38
 */
@Data
@Accessors(chain = true)
public class CheckEntity {

    @Matcher(value = {"杭州", "郑州"})
    private String address;
    @Check
    @Matcher(customize = "com.simonalong.mikilin.check.ValidateCheck#haveRepeat", accept = false)
    private List<CheckInnerEntity> innerEntityList;
}
