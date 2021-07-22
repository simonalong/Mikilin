package com.simonalong.mikilin.match.matcher;

import java.text.MessageFormat;

/**
 * @author zhouzhenyong
 * @since 2019/4/13 下午11:04
 */
public abstract class AbstractBlackWhiteMatch implements Match {

    private String blackMsg;
    private String whiteMsg;

    void setBlackMsg(String pattern, Object... arguments){
        this.blackMsg = MessageFormat.format(pattern, arguments);
    }

    void setWhiteMsg(String pattern, Object... arguments){
        this.whiteMsg = MessageFormat.format(pattern, arguments);
    }

    @Override
    public String getBlackMsg(){
        return blackMsg;
    }

    @Override
    public String getWhiteMsg(){
        return whiteMsg;
    }
}
