package com.simonalong.mikilin.judge;

import com.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 上午11:18
 */
@Data
@Accessors(chain = true)
public class JudgeEntity {

    @FieldWhiteMatcher(judge = "JudgeCheck#ageValid")
    private Integer age;

    @FieldWhiteMatcher(judge = "JudgeCheck#nameValid")
    private String name;

    @FieldBlackMatcher(judge = "JudgeCheck#addressInvalid")
    private String address;

    @FieldWhiteMatcher(judge = "JudgeCheck#ratioJudge")
    private Float mRatio;

    private Float nRatio;
}
