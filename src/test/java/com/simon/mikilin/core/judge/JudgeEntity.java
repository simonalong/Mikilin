package com.simon.mikilin.core.judge;

import com.simon.mikilin.core.annotation.FieldBlackMatcher;
import com.simon.mikilin.core.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 上午11:18
 */
@Data
@Accessors(chain = true)
public class JudgeEntity {

    @FieldWhiteMatcher(judge = "com.simon.mikilin.core.judge.JudgeCheck#ageValid")
    private Integer age;

    @FieldWhiteMatcher(judge = "com.simon.mikilin.core.judge.JudgeCheck#nameValid")
    private String name;

    @FieldBlackMatcher(judge = "com.simon.mikilin.core.judge.JudgeCheck#addressInvalid")
    private String address;

    @FieldWhiteMatcher(judge = "com.simon.mikilin.core.judge.JudgeCheck#ratioJudge")
    private Float mRatio;

    private Float nRatio;
}
