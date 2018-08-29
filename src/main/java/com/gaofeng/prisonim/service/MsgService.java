package com.gaofeng.prisonim.service;

import com.didi.meta.javalib.IdUtil;
import com.didi.meta.javalib.JLog;
import com.gaofeng.prisonDBlib.beans.msgrecord.SendType;
import com.gaofeng.prisonDBlib.model.MessageRecord;
import com.gaofeng.prisonDBlib.model.MessageRecordMapper;
import com.gaofeng.prisonim.beans.msg.SendReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: gaofeng
 * @Date: 2018-08-26
 * @Description:
 */
@Service
public class MsgService {

    private MessageRecordMapper mrm;

    @Autowired
    public MsgService(MessageRecordMapper messageRecordMapper) {
        this.mrm = messageRecordMapper;
    }

    /**
     * 消息发送
     *
     * @param sendReq
     *
     * @return
     */
    public boolean send(SendReq sendReq) {
        JLog.info("msg send service msgType=" + sendReq.getMessageType());
        // 构建消息发送对象
        MessageRecord messageRecord = new MessageRecord();
        messageRecord.setRecordId(IdUtil.generateLongId());
        messageRecord.setSpokeId(sendReq.getSpokeId());
        messageRecord.setReceiverId(sendReq.getReceiverId());
        messageRecord.setSendType(sendReq.getSendType());
        messageRecord.setMessageType(sendReq.getMessageType());
        messageRecord.setContent(sendReq.getContent());
        // 发送消息时，消息状态是已提交等待审核
        messageRecord.setMessageStatus(MessageRecord.MessageStatus.WAIT_AUDIT);
        // 发送时间设置为当前时间 秒级
        messageRecord.setSendTime(System.currentTimeMillis() / 1000);

        Integer saveRet = mrm.save(messageRecord);
        if (!saveRet.equals(1)) {
            JLog.error("save messageRecord failed spokerId=" + sendReq.getSpokeId() + " sendType" +
                    "=" + sendReq.getSendType(), 104260927);
            return false;
        }

        // 消息发送成功，应push一条消息，改变消息统计数据

        return true;
    }


    /**
     * 拉取需要审核的消息
     *
     * @return
     */
    public List<SendType> auditPull() {
        List<SendType> sendTypes = new ArrayList<>();
        try {
            sendTypes = mrm.findAuditMsgRecord();
        } catch (Exception e) {
            JLog.error("pull audit msgs db error errMsg=" + e.getMessage(), 104290954);
        }
        return sendTypes;
    }

    /**
     * 聊天记录批量通过/不通过审核
     *
     * @param messageStatus
     * @param recordIds
     *
     * @return 1:成功 2：含有非待审核记录 3：数据库错误
     */
    @Transactional
    public Integer audit(Integer messageStatus, List<Long> recordIds) {
        JLog.info("auditPass service recordIds size=" + recordIds.size());
        // 查找出待审核记录
        List<Long> waitAuditRecords = mrm.findWaitAuditRecords();
        // 判断待要更改的记录id是否在所有待审核记录id的子集中
        if (!waitAuditRecords.containsAll(recordIds)) {
            JLog.error("illegal recordIds=" + Arrays.toString(recordIds.toArray()), 104291138);
            return 2;
        }
        Integer updateRet = mrm.batchUpdateMessageStatusByRecordId(messageStatus, recordIds);
        // 批量更新失败，回滚事务，返回false
        if (!updateRet.equals(recordIds.size())) {
            JLog.error("db error batch update messageStatus failed updateRet=" + updateRet,
                    104291054);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 3;
        }
        return 1;
    }
}
