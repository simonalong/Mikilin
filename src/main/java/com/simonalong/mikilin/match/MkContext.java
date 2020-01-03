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
        initErrMsg();
    }

    public String getErrMsgChain() {
        String msg = errMsgChain.get().toString();
        initErrMsg();
        return msg;
    }

    public void append(String errMsgStr, Object... keys) {
        errMsgChain.get().append("-->").append(MessageFormat.format(errMsgStr, keys));
    }

    public void append(String errMsgStr) {
        errMsgChain.get().append("-->").append(errMsgStr);
    }

    public void append(List<String> errMsgList) {
        if (CollectionUtil.isEmpty(errMsgList)) {
            return;
        }
        errMsgList.forEach(this::append);
    }

    public void setLastErrMsg(String errMsg) {
        this.theLastErrMsg.set(errMsg);
    }

    public String getLastErrMsg() {
        return theLastErrMsg.get();
    }

    private void initErrMsg() {
        errMsgChain.remove();
        errMsgChain.set(new StringBuilder().append("数据校验失败"));
    }
}
