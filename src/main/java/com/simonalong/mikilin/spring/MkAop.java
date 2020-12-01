package com.simonalong.mikilin.spring;

import com.simonalong.mikilin.MkConstant;
import com.simonalong.mikilin.MkValidators;
import com.simonalong.mikilin.annotation.AutoCheck;
import com.simonalong.mikilin.exception.MkException;
import com.simonalong.mikilin.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author robot
 */
@Slf4j
@Aspect
public class MkAop {

    /**
     * 拦截添加注解的方法
     * @param pjp 参数
     * @return 执行的返回结果
     * @throws Throwable 异常情况下返回
     */
    @Around("@within(com.simonalong.mikilin.annotation.AutoCheck) || @annotation(com.simonalong.mikilin.annotation.AutoCheck)")
    public Object aroundParameter(ProceedingJoinPoint pjp) throws Throwable {
        String funStr = pjp.getSignature().toLongString();
        Object result;
        try {
            validate(pjp);
            result = pjp.proceed();
            MkValidators.validate(result);
        } catch (Throwable e) {
            MkException mkException = ExceptionUtil.getCause(e, MkException.class);
            if (null != mkException) {
                mkException.setFunStr(funStr);
                mkException.setParameterList(getParameters(pjp));
                throw mkException;
            } else {
                throw e;
            }
        }
        return result;
    }

    private List<Object> getParameters(ProceedingJoinPoint pjp) {
        List<Object> parameters = new ArrayList<>();
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof MultipartFile) {
                continue;
            }
            parameters.add(arg);
        }
        return parameters;
    }

    private void validate(ProceedingJoinPoint pjp) {
        Signature sig = pjp.getSignature();
        MethodSignature methodSignature;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        methodSignature = (MethodSignature) sig;
        Method currentMethod;
        try {
            currentMethod = pjp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new MkException(e);
        }

        AutoCheck autoCheck = null;
        if (currentMethod.getDeclaringClass().isAnnotationPresent(AutoCheck.class)) {
            autoCheck = currentMethod.getDeclaringClass().getAnnotation(AutoCheck.class);
        }

        if (currentMethod.isAnnotationPresent(AutoCheck.class)) {
            autoCheck = currentMethod.getAnnotation(AutoCheck.class);
        }

        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof MultipartFile) {
                continue;
            }

            if (null != autoCheck) {
                String group = autoCheck.group();
                if (group.equals(MkConstant.DEFAULT_GROUP)) {
                    group = autoCheck.value();
                }
                MkValidators.validate(group, arg);
            }
        }
    }
}

