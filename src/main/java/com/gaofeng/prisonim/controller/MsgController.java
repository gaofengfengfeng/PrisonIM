package com.gaofeng.prisonim.controller;

import apple.laf.JRSUIConstants;
import com.didi.meta.javalib.JLog;
import com.didi.meta.javalib.JResponse;
import com.gaofeng.prisonDBlib.beans.msgrecord.WaitReadMsgDetail;
import com.gaofeng.prisonDBlib.beans.msgrecord.WaitReadMsgs;
import com.gaofeng.prisonDBlib.model.MessageRecord;
import com.gaofeng.prisonim.beans.msg.MsgStatusChangeReq;
import com.gaofeng.prisonim.beans.msg.PullReq;
import com.gaofeng.prisonim.beans.msg.PullUnpassReq;
import com.gaofeng.prisonim.beans.msg.SendReq;
import com.gaofeng.prisonim.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: gaofeng
 * @Date: 2018-08-25
 * @Description: 监狱系统中留言体系
 */
@RestController
@RequestMapping(value = "/im")
public class MsgController {

    private MsgService ms;

    @Autowired
    public MsgController(MsgService msgService) {
        this.ms = msgService;
    }

    /**
     * 消息发送
     *
     * @param request
     * @param sendReq
     *
     * @return
     */
    @RequestMapping(value = "/send")
    public JResponse send(HttpServletRequest request, @RequestBody @Valid SendReq sendReq) {
        JLog.info("im send spokerId=" + sendReq.getSpokeId() + " receiverId=" +
                sendReq.getReceiverId() + " sendType=" + sendReq.getSendType());
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);

        // 对内容的合法性进行验证

        // 插入聊天记录
        boolean saveRet = ms.send(sendReq);

        if (!saveRet) {
            jResponse.setErrNo(104260927);
            jResponse.setErrMsg("db error");
        }

        return jResponse;
    }

    /**
     * 未读消息拉取
     *
     * @param request
     * @param pullReq
     *
     * @return
     */
    @RequestMapping(value = "/pull")
    public JResponse pull(HttpServletRequest request, @RequestBody @Valid PullReq pullReq) {
        JLog.info("msg pull receiverId=" + pullReq.getReceiverId());
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);

        List<WaitReadMsgs> waitReadMsgs = ms.pullMsgs(pullReq.getReceiverId());

        if (waitReadMsgs == null) {
            jResponse.setErrNo(104302313);
            jResponse.setErrMsg("db error");
            return jResponse;
        }

        jResponse.setData(waitReadMsgs);
        return jResponse;
    }

    /**
     * 审核未通过消息拉取
     *
     * @param request
     * @param pullUnpassReq
     *
     * @return
     */
    @RequestMapping(value = "/pullUnpass")
    public JResponse pullUnpass(HttpServletRequest request,
                                @RequestBody @Valid PullUnpassReq pullUnpassReq) {
        JLog.info("pullUnpass spokeId=" + pullUnpassReq.getSpokeId());
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);

        List<WaitReadMsgDetail> unpassMsgs = ms.pullUnpassMsgs(pullUnpassReq.getSpokeId());

        if (unpassMsgs == null) {
            jResponse.setErrNo(104311522);
            jResponse.setErrMsg("db error");
            return jResponse;
        }

        jResponse.setData(unpassMsgs);
        return jResponse;
    }

    @RequestMapping(value = "arrived")
    public JResponse arrived(HttpServletRequest request,
                             @RequestBody @Valid MsgStatusChangeReq msgStatusChangeReq) {
        JLog.info("im arrived recordIds=" + Arrays.toString(msgStatusChangeReq.
                getRecordIdAndStatuses().toArray()));
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);

        // 对送达的消息进行分组，分为审核通过和不通过消息
        List<Long> auditPassRecords = new ArrayList<>();
        List<Long> auditUnpassRecords = new ArrayList<>();
        // 遍历请求来的list，进行分组
        for (MsgStatusChangeReq.RecordIdAndStatus recordIdAndStatus :
                msgStatusChangeReq.getRecordIdAndStatuses()) {
            if (recordIdAndStatus.getMessageStatus().equals(MessageRecord.MessageStatus.AUDIT_PASS)) {
                auditPassRecords.add(recordIdAndStatus.getRecordId());
            } else if (recordIdAndStatus.getMessageStatus().equals(MessageRecord.MessageStatus.
                    AUDIT_FAILD)) {
                auditUnpassRecords.add(recordIdAndStatus.getRecordId());
            } else {
                jResponse.setErrNo(104311955);
                jResponse.setErrMsg("messageStatus should be 2 or 3");
                return jResponse;
            }
        }

        Integer updateRet;
        // 判断两个list是否都为空
        if (auditPassRecords.size() == 0 && auditUnpassRecords.size() == 0) {
            jResponse.setErrNo(104312018);
            jResponse.setErrMsg("legal records id null");
            return jResponse;
        } else {
            // 对状态进行更新
            updateRet = ms.messageStatusChange2Received(auditPassRecords, auditUnpassRecords);
        }

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

    @RequestMapping(value = "/read")
    public JResponse read(HttpServletRequest request,
                          @RequestBody @Valid MsgStatusChangeReq msgStatusChangeReq) {
        JLog.info("im read recordIds=" + Arrays.toString(msgStatusChangeReq.
                getRecordIdAndStatuses().toArray()));
        JResponse jResponse = JResponse.initResponse(request, JResponse.class);

        // 对送达的消息进行分组，分为审核通过和不通过消息
        List<Long> auditPassRecords = new ArrayList<>();
        List<Long> auditUnpassRecords = new ArrayList<>();
        // 遍历请求来的list，进行分组
        for (MsgStatusChangeReq.RecordIdAndStatus recordIdAndStatus :
                msgStatusChangeReq.getRecordIdAndStatuses()) {
            if (recordIdAndStatus.getMessageStatus().equals(MessageRecord.MessageStatus.RECEIVED)) {
                auditPassRecords.add(recordIdAndStatus.getRecordId());
            } else if (recordIdAndStatus.getMessageStatus().equals(MessageRecord.MessageStatus.
                    UNPASS_RESULT_RECEIVED)) {
                auditUnpassRecords.add(recordIdAndStatus.getRecordId());
            } else {
                jResponse.setErrNo(104311955);
                jResponse.setErrMsg("messageStatus should be 4 or 6");
                return jResponse;
            }
        }

        Integer updateRet;
        // 判断两个list是否都为空
        if (auditPassRecords.size() == 0 && auditUnpassRecords.size() == 0) {
            jResponse.setErrNo(104312243);
            jResponse.setErrMsg("legal records id null");
            return jResponse;
        } else {
            // 对状态进行更新
            updateRet = ms.messageStatusChange2Read(auditPassRecords, auditUnpassRecords);
        }

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
