package com.gaofeng.prisonim;

import com.alibaba.fastjson.JSONObject;
import com.didi.meta.javalib.JResponse;
import com.gaofeng.prisonim.beans.common.BaseReq;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @Author:gaofeng
 * @Date:2018/5/24
 * @Description: 网络请求，请求controller方法经历的切面
 **/
@Aspect
@Configuration
public class WebLogicAspect {

    @Pointcut("execution(public * com.gaofeng.prisonim.controller..*(..)) && @annotation" +
            "(org.springframework.web.bind.annotation.RequestMapping)")
    public void weblogic() {
    }

    @Around("weblogic()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // 取到request请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();

        // 取出在JMessageConverter中放置的 _requestBody属性，里面是请求体字符串
        String requestBody = (String) request.getAttribute("_requestBody");

        // 请求都是继承了 BaseReq的，可以从中取得token信息
        BaseReq baseReq = JSONObject.parseObject(requestBody, BaseReq.class);

        // 取得拦截方法的返回类型
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        // 判断返回类型是否是 JResponse的子类
        boolean isFather = JResponse.class.isAssignableFrom(targetMethod.getReturnType());

        // JResponse jResponse = (JResponse) targetMethod.getReturnType().newInstance();
        // jResponse.setErrNo(10000000);
        // jResponse.setErrMsg("aspect");
        // return jResponse;
        return proceedingJoinPoint.proceed();
    }

    @AfterReturning(returning = "jResponse", pointcut = "weblogic()")
    public void doAfterReturning(JResponse jResponse) throws Throwable {
        jResponse.setServerTime(System.currentTimeMillis() / 1000);
    }


}
