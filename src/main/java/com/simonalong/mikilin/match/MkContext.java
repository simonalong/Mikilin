package com.simonalong.mikilin.match;

import com.simonalong.mikilin.util.CollectionUtil;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author zhouzhenyong
 * @since 2019-08-12 20:52
 */
public class MkContext {

    /**
     * 本次请求的函数的入参
     */
    private final ThreadLocal<Object> parameterReq;
    /**
     * 错误信息链
     */
    private final ThreadLocal<StringBuilder> errMsgChain;
    /**
     * 最后的那个错误信息
     */
    private final ThreadLocal<String> theLastErrMsg;

    public MkContext() {
        parameterReq = new ThreadLocal<>();
        errMsgChain = new ThreadLocal<>();
        theLastErrMsg = new ThreadLocal<>();
    }

    public void setParameter(Object parameter) {
        parameterReq.set(parameter);
    }

    public Object getParameter() {
        return parameterReq.get();
    }

    public String getErrMsgChain() {
        return errMsgChain.get().toString();
    }

    public void append(String errMsgStr, Object... keys) {
        if(null == errMsgStr){
            return;
        }
        errMsgChain.get().append("-->").append(MessageFormat.format(errMsgStr, keys));
    }

    public void append(String errMsgStr) {
        if(null == errMsgStr){
            return;
        }
        errMsgChain.get().append("-->").append(errMsgStr);
    }

    public void append(List<String> errMsgList) {
        if (CollectionUtil.isEmpty(errMsgList)) {
            return;
        }

        append(String.join("，而且", errMsgList));
    }

    public void setLastErrMsg(String errMsg) {
        if(null == errMsg){
            return;
        }
        this.theLastErrMsg.set(errMsg);
    }

    public String getLastErrMsg() {
        return theLastErrMsg.get();
    }

    public void clear() {
        parameterReq.remove();
        errMsgChain.remove();
        theLastErrMsg.remove();
        errMsgChain.set(new StringBuilder().append("数据校验失败："));
    }
}
