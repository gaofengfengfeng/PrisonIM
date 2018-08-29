package com.gaofeng.prisonim.controller;

import com.didi.meta.javalib.JLog;
import com.didi.meta.javalib.JResponse;
import com.gaofeng.prisonDBlib.beans.msgrecord.SendType;
import com.gaofeng.prisonim.beans.audit.AuditPullReq;
import com.gaofeng.prisonim.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: gaofeng
 * @Date: 2018-08-29
 * @Description: 留言系统审核
 */
@RestController
@RequestMapping(value = "/audit")
public class AuditControler {

    private MsgService ms;

    @Autowired
    public AuditControler(MsgService msgService) {
        this.ms = msgService;
    }

    /**
     * 拉去审核消息
     *
     * @param request
     * @param auditPullReq
     *
     * @return
     */
    @RequestMapping(value = "/pull")
    public JResponse auditPull(HttpServletRequest request,
                               @RequestBody @Valid AuditPullReq auditPullReq) {
        JLog.info("auditPull policeId=" + auditPullReq.getPoliceId());
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);

        // 拉取审核记录，然后数据库发生异常，返回的sendTypes为null
        List<SendType> sendTypes = ms.auditPull();
        if (sendTypes == null) {
            jResponse.setErrNo(104290954);
            jResponse.setErrMsg("db error");
        } else {
            jResponse.setData(sendTypes);
        }
        return jResponse;
    }

    @RequestMapping(value = "/pass")
    public JResponse auditPass(HttpServletRequest request) {
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);
        return jResponse;
    }

}
