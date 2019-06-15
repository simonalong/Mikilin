package com.github.simonalong.mikilin.judge;

import com.github.simonalong.mikilin.annotation.FieldBlackMatcher;
import com.github.simonalong.mikilin.annotation.FieldWhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 上午11:18
 */
@Data
@Accessors(chain = true)
public class JudgeEntity {

    @FieldWhiteMatcher(judge = "com.github.simonalong.mikilin.judge.JudgeCheck#ageValid")
    private Integer age;

    @FieldWhiteMatcher(judge = "com.github.simonalong.mikilin.judge.JudgeCheck#nameValid")
    private String name;

    @FieldBlackMatcher(judge = "com.github.simonalong.mikilin.judge.JudgeCheck#addressInvalid")
    private String address;

    @FieldWhiteMatcher(judge = "com.github.simonalong.mikilin.judge.JudgeCheck#ratioJudge")
    private Float mRatio;

    private Float nRatio;
}
