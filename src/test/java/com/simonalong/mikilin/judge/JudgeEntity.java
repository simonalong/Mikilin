package com.simonalong.mikilin.judge;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 上午11:18
 */
@Data
@Accessors(chain = true)
public class JudgeEntity {

    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#ageValid")
    private Integer age;

    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#nameValid")
    private String name;

    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#addressInvalid", acceptOrDeny = false)
    private String address;

    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#ratioJudge")
    private Float mRatio;

    private Float nRatio;

    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#twoParam")
    private String twoPa;

    @Matcher(judge = "com.simonalong.mikilin.judge.JudgeCheck#threeParam")
    private String threePa;
}
