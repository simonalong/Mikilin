package com.simonalong.mikilin.customize;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 上午11:18
 */
@Data
@Accessors(chain = true)
public class CustomizeEntity {

    @Matcher(customize = "com.simonalong.mikilin.customize.CustomizeCheck#ageValid")
    private Integer age;

    @Matcher(customize = "com.simonalong.mikilin.customize.CustomizeCheck#nameValid")
    private String name;

    @Matcher(customize = "com.simonalong.mikilin.customize.CustomizeCheck#addressInvalid", accept = false)
    private String address;

    @Matcher(customize = "com.simonalong.mikilin.customize.CustomizeCheck#ratioJudge")
    private Float mRatio;

    private Float nRatio;

    @Matcher(customize = "com.simonalong.mikilin.customize.CustomizeCheck#twoParam")
    private String twoPa;

    @Matcher(customize = "com.simonalong.mikilin.customize.CustomizeCheck#threeParam")
    private String threePa;

    /**
     * 错误路径模拟
     * <p>这里先注释用于test能通过
     */
    //@Matcher(customize = "com.xxx.xxx.xxx.CustomizeCheck#errPathTest")
    private String errPath;
}
