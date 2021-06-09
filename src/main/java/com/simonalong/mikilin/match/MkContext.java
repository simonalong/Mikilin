package com.simonalong.mikilin.match;

import com.simonalong.mikilin.util.CollectionUtil;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.simonalong.mikilin.MkConstant.PARENT_KEY;

/**
 * @author zhouzhenyong
 * @since 2019-08-12 20:52
 */
public class MkContext {

    /**
     * 本次请求的函数的入参
     */
    private final ThreadLocal<Object> parameterReqLocal;
    /**
     * 错误信息链
     */
    private final ThreadLocal<StringBuilder> errMsgChainLocal;
    /**
     * 错误map
     */
    private final ThreadLocal<Map<String, Object>> errMsgMapLocal;
    /**
     * 错误堆栈
     */
    private final ThreadLocal<Deque<Map<String, Object>>> errMsgDequeLocal;
    /**
     * 最后的那个错误信息
     */
    private final ThreadLocal<String> theLastErrMsgLocal;

    public MkContext() {
        parameterReqLocal = new ThreadLocal<>();
        errMsgChainLocal = new ThreadLocal<>();
        theLastErrMsgLocal = new ThreadLocal<>();
        errMsgMapLocal = new ThreadLocal<>();
        errMsgDequeLocal = new ThreadLocal<>();
    }

    public void setParameter(Object parameter) {
        parameterReqLocal.set(parameter);
    }

    public Object getParameter() {
        return parameterReqLocal.get();
    }

    public String getErrMsgChainLocal() {
        return errMsgChainLocal.get().toString();
    }

    public void append(String errMsgStr, Object... keys) {
        if(null == errMsgStr){
            return;
        }
        errMsgChainLocal.get().append("-->").append(MessageFormat.format(errMsgStr, keys));
    }

    public void beforeErrMsg() {
        Map<String, Object> errMsgMap = new ConcurrentHashMap<>();
        Deque<Map<String, Object>> deque = errMsgDequeLocal.get();
        if(null == deque) {
            deque = new LinkedList<>();
            deque.push(errMsgMap);
            errMsgDequeLocal.set(deque);
        } else {
            deque.push(errMsgMap);
        }
    }

    protected void putKeyAndErrMsg(String fieldName, String errMsg) {
        if (null == errMsg || "".equals(errMsg)) {
            return;
        }
        Map<String, Object> errMsgMap = errMsgDequeLocal.get().peek();
        if (null != errMsgMap) {
            errMsgMap.putIfAbsent(fieldName, errMsg);
        } else {
            errMsgMap = new ConcurrentHashMap<>();
            errMsgMap.put(fieldName, errMsg);
            errMsgDequeLocal.get().push(errMsgMap);
        }
    }

    public void flush(String parentKey) {
        Map<String, Object> errMsgMap = errMsgDequeLocal.get().poll();
        if (null == errMsgMap || errMsgMap.isEmpty()) {
            return;
        }

        Map<String, Object> parentMsgMap = errMsgDequeLocal.get().peek();
        if (null == parentMsgMap) {
            parentMsgMap = new ConcurrentHashMap<>();
        }

        parentMsgMap.put(parentKey, errMsgMap);
        errMsgMapLocal.set(parentMsgMap);
    }

    public void poll() {
        Deque<Map<String, Object>> deque = errMsgDequeLocal.get();
        deque.poll();
    }

    public void append(String errMsgStr) {
        if(null == errMsgStr){
            return;
        }
        errMsgChainLocal.get().append("-->").append(errMsgStr);
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
        this.theLastErrMsgLocal.set(errMsg);
    }

    public String getLastErrMsg() {
        return theLastErrMsgLocal.get();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getErrMsgMap() {
        Map<String, Object> rootErrMsgMap = errMsgMapLocal.get();
        if (null != rootErrMsgMap && !rootErrMsgMap.isEmpty()) {
            return (Map<String, Object>) rootErrMsgMap.get(PARENT_KEY);
        } else {
            return new HashMap<>();
        }
    }

    public void clearParameter() {
        parameterReqLocal.remove();
    }

    public void clearErrMsgMap() {
        errMsgDequeLocal.remove();
        errMsgMapLocal.remove();
    }

    public void clearLog() {
        errMsgChainLocal.remove();
        theLastErrMsgLocal.remove();
        errMsgChainLocal.set(new StringBuilder().append("数据校验失败："));
    }

    public void clearAll() {
        clearParameter();
        clearLog();
        clearErrMsgMap();
    }
}
