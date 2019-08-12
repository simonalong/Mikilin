package com.simonalong.mikilin.match;

import java.text.MessageFormat;

/**
 * @author zhouzhenyong
 * @since 2019-08-12 13:52
 */
public class MkContext {

    private ThreadLocal<StringBuilder> errMsg;

    public MkContext(){
        errMsg = new ThreadLocal<>();
        initErrMsg();
    }

    public void init(){
        initErrMsg();
    }

    public String getErrMsg(){
        String msg = errMsg.get().toString();
        initErrMsg();
        return msg;
    }

    public void append(String errMsgStr, Object... keys){
        errMsg.get().append("-->").append(MessageFormat.format(errMsgStr, keys));
    }

    public void append(String errMsgStr){
        errMsg.get().append("-->").append(errMsgStr);
    }

    private void initErrMsg(){
        errMsg.remove();
        errMsg.set(new StringBuilder().append("数据校验失败"));
    }
}
