package com.simonalong.mikilin.customize;

import com.simonalong.mikilin.match.MkContext;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhouzhenyong
 * @since 2019/3/10 下午11:19
 */
public class CustomizeCheck {

    /**
     * 年龄是否合法
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
     * 名称是否合法
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

    /**
     * 地址匹配
     */
    private boolean addressInvalid(String address){
        if(null == address){
            return true;
        }
        List<String> blackList = Arrays.asList("beijing", "hangzhou");
        if (blackList.contains(address)) {
            return true;
        }
        return false;
    }

    /**
     * 能够传递核查的对象，对于对象中的一些属性可以进行系统内部的配置
     *
     * mRatio + nRatio < 1.0
     */
    private boolean ratioJudge(CustomizeEntity customizeEntity, Float nRatio) {
        if(null == nRatio || null == customizeEntity){
            return false;
        }
        return nRatio + customizeEntity.getMRatio() < 10.0f;
    }

    /**
     * 两个函数
     */
    private boolean twoParam(String funName, MkContext context) {
        if (funName.equals("hello")){
            context.append("匹配上字段'hello'");
           return true;
        }
        context.append("没有匹配上字段'hello'");
        return false;
    }

    /**
     * 三个函数，参数顺序随意
     */
    private boolean threeParam(MkContext context, CustomizeEntity customizeEntity, String temK) {
        if (temK.equals("hello") || temK.equals("word")){
            context.append("匹配上字段'hello'和'word'");
            return true;
        }
        context.append("没有匹配上字段'hello'和'word'");
        return false;
    }

    /**
     * 三个函数2，参数顺序随意
     */
    private boolean threeParam2(MkContext context, String temK, CustomizeEntity customizeEntity) {
        if (temK.equals("hello") || temK.equals("word")){
            context.append("匹配上字段'hello'和'word'");
            return true;
        }
        context.append("没有匹配上字段'hello'和'word'");
        return false;
    }

    private boolean fieldErrMsgMatch(String fieldErrMsg, MkContext mkContext) {
        if (fieldErrMsg.contains("mock")) {
            mkContext.setLastErrMsg("当前的值命中黑名单");
            return true;
        } else {
            mkContext.setLastErrMsg("当前的值不符合需求");
            return false;
        }
    }

    private boolean fieldErrMsgMatch2(String fieldErrMsg, MkContext mkContext) {
        if (fieldErrMsg.contains("mock")) {
            mkContext.setLastErrMsg("当前的值命中黑名单");
            return true;
        } else {
            mkContext.setLastErrMsg("当前的值不符合需求");
            return false;
        }
    }

    private boolean fieldErrMsgMatch3(String fieldErrMsg, MkContext mkContext) {
        return fieldErrMsg.contains("mock");
    }
}
