package com.gaofeng.prisonim.controller;

import com.didi.meta.javalib.JLog;
import com.didi.meta.javalib.JResponse;
import com.gaofeng.prisonDBlib.beans.audit.SendType;
import com.gaofeng.prisonDBlib.model.MessageRecord;
import com.gaofeng.prisonim.beans.audit.AuditPullReq;
import com.gaofeng.prisonim.beans.audit.AuditReq;
import com.gaofeng.prisonim.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
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

    /**
     * 信息审核通过
     *
     * @param request
     * @param auditReq
     *
     * @return
     */
    @RequestMapping(value = "/pass")
    public JResponse auditPass(HttpServletRequest request, @RequestBody @Valid AuditReq auditReq) {
        JLog.info("auditPass policeId=" + auditReq.getPoliceId() + " recordIds=" +
                Arrays.toString(auditReq.getRecordIds().toArray()));
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);

        // 更新数据状态
        Integer updateRet = ms.audit(MessageRecord.MessageStatus.AUDIT_PASS,
                auditReq.getRecordIds());

        // 1:成功 2：含有非待审核记录 3：数据库错误
        switch (updateRet) {
            case 1:
                break;
            case 2:
                jResponse.setErrNo(104291138);
                jResponse.setErrMsg("illegal recordIds");
                break;
            case 3:
                jResponse.setErrNo(104291054);
                jResponse.setErrMsg("db error");
                break;
            default:
                jResponse.setErrNo(104291141);
                jResponse.setErrMsg("unknown exception");
                break;
        }

        return jResponse;
    }

    /**
     * 信息审核不通过
     *
     * @param request
     * @param auditReq
     *
     * @return
     */
    @RequestMapping(value = "/unpass")
    public JResponse auditUnpass(HttpServletRequest request,
                                 @RequestBody @Valid AuditReq auditReq) {
        JLog.info("auditUnpass policeId=" + auditReq.getPoliceId() + " recordIds=" +
                Arrays.toString(auditReq.getRecordIds().toArray()));
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);

        // 更新数据状态
        Integer updateRet = ms.audit(MessageRecord.MessageStatus.AUDIT_FAILD,
                auditReq.getRecordIds());

        // 1:成功 2：含有非待审核记录 3：数据库错误
        switch (updateRet) {
            case 1:
                break;
            case 2:
                jResponse.setErrNo(104291138);
                jResponse.setErrMsg("illegal recordIds");
                break;
            case 3:
                jResponse.setErrNo(104291054);
                jResponse.setErrMsg("db error");
                break;
            default:
                jResponse.setErrNo(104291141);
                jResponse.setErrMsg("unknown exception");
                break;
        }

        return jResponse;
    }
}
