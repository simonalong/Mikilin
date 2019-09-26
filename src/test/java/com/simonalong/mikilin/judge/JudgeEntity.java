package com.simonalong.mikilin.judge;

import com.simonalong.mikilin.annotation.BlackMatcher;
import com.simonalong.mikilin.annotation.WhiteMatcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 上午11:18
 */
@Data
@Accessors(chain = true)
public class JudgeEntity {

    @WhiteMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#ageValid")
    private Integer age;

    @WhiteMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#nameValid")
    private String name;

    @BlackMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#addressInvalid")
    private String address;

    @WhiteMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#ratioJudge")
    private Float mRatio;

    private Float nRatio;

    @WhiteMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#twoParam")
    private String twoPa;

    @WhiteMatcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#threeParam")
    private String threePa;
}
