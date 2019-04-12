package com.simon.mikilin.core.match;

import com.simon.mikilin.core.annotation.FieldValidCheck;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 上午11:18
 */
@Data
@Accessors(chain = true)
public class JudgeEntity {

    @FieldValidCheck(judge = "com.simon.mikilin.core.match.JudgeCls#ageValid")
    private Integer age;

    @FieldValidCheck(judge = "com.simon.mikilin.core.match.JudgeCls#nameValid")
    private String name;
}
