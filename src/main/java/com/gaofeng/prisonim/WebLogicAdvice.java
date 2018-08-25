package com.gaofeng.prisonim;

import com.didi.meta.javalib.JErr;
import com.didi.meta.javalib.JLog;
import com.didi.meta.javalib.JResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:gaofeng
 * @Date:2018/5/24
 * @Description：异常处理类
 **/
@ControllerAdvice
public class WebLogicAdvice {

    /**
     * @Author:gaofeng
     * @Date:2018/5/24
     * @Description: 在方法处理过程中遇到异常时，集中处理异常
     **/
    public JResponse errorHandler(HttpServletRequest request, Exception exception) {
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);

        if (exception.getClass().isAssignableFrom(MethodArgumentNotValidException.class)) {
            jResponse.setErrMsg("param error");
            jResponse.setErrNo(JErr.PARAM_ERR);
        } else {
            Integer errNo = 100142227;
            JLog.error(exception.getMessage(), errNo);
            jResponse.setErrNo(errNo);
            jResponse.setErrMsg("server busy");
        }
        jResponse.setServerTime(System.currentTimeMillis());
        return jResponse;
    }
}
