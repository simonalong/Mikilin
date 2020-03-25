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
     * 错误信息链
     */
    private ThreadLocal<StringBuilder> errMsgChain;
    /**
     * 最后的那个错误信息
     */
    private ThreadLocal<String> theLastErrMsg;

    public MkContext() {
        errMsgChain = new ThreadLocal<>();
        theLastErrMsg = new ThreadLocal<>();
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

    public void set() {
        errMsgChain.remove();
        theLastErrMsg.remove();
        errMsgChain.set(new StringBuilder().append("数据校验失败："));
    }

    public void clear() {
        errMsgChain.remove();
        theLastErrMsg.remove();
        errMsgChain.set(new StringBuilder().append("数据校验失败："));
    }
}
