package com.simon.mikilin.core.judge;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 上午11:19
 */
public class JudgeCls {

    /**
     * 年龄可用
     */
    public boolean ageValid(Integer age) {
        if(null == age){
            return false;
        }
        if (age >= 0 && age < 200) {
            return true;
        }

        return false;
    }

    /**
     * 如果名字位于黑名单中，则命中失败
     */
    private boolean nameValid(String name) {
        if(null == name){
            return false;
        }
        List<String> blackList = Arrays.asList("women", "haode");
        if (blackList.contains(name)) {
            return false;
        }
        return true;
    }
}
