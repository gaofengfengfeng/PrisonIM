package com.gaofeng.prisonim.service;

import com.didi.meta.javalib.IdUtil;
import com.didi.meta.javalib.JLog;
import com.gaofeng.prisonDBlib.beans.audit.SendType;
import com.gaofeng.prisonDBlib.beans.msgrecord.WaitReadMsgDetail;
import com.gaofeng.prisonDBlib.beans.msgrecord.WaitReadMsgs;
import com.gaofeng.prisonDBlib.model.MessageRecord;
import com.gaofeng.prisonDBlib.model.MessageRecordMapper;
import com.gaofeng.prisonim.beans.msg.SendReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
            return null;
        }
        return sendTypes;
    }

    /**
     * 更新消息为已送达状态
     *
     * @param auditPassRecords
     * @param auditUnpassRecords
     *
     * @return
     */
    @Transactional
    public Integer messageStatusChange2Received(List<Long> auditPassRecords,
                                                List<Long> auditUnpassRecords) {
        JLog.info("messageStatusChange2Received auditPassRecords size=" + auditPassRecords.size()
                + " auditUnpassRecords size=" + auditUnpassRecords.size());
        Integer updateRet = null;
        // 如果相应的list不为0， 则进行相应的更新操作
        if (auditPassRecords.size() != 0) {
            updateRet = messageStatusChange(MessageRecord.MessageStatus.RECEIVED, auditPassRecords);
            if (updateRet != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return updateRet;
            }
        }

        if (auditUnpassRecords.size() != 0) {
            updateRet = messageStatusChange(MessageRecord.MessageStatus.UNPASS_RESULT_RECEIVED,
                    auditUnpassRecords);
            if (updateRet != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return updateRet;
            }
        }

        return updateRet;
    }

    public Integer messageStatusChange2Read(List<Long> auditPassRecords,
                                            List<Long> auditUnpassRecords) {
        JLog.info("messageStatusChange2Read auditPassRecords size=" + auditPassRecords.size()
                + " auditUnpassRecords size=" + auditUnpassRecords.size());
        Integer updateRet = null;
        // 如果相应的list不为0， 则进行相应的更新操作
        if (auditPassRecords.size() != 0) {
            updateRet = messageStatusChange(MessageRecord.MessageStatus.READ, auditPassRecords);
            if (updateRet != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return updateRet;
            }
        }

        if (auditUnpassRecords.size() != 0) {
            updateRet = messageStatusChange(MessageRecord.MessageStatus.UNPASS_RESULT_READ,
                    auditUnpassRecords);
            if (updateRet != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return updateRet;
            }
        }

        return updateRet;
    }

    /**
     * 更新消息的状态
     *
     * @param messageStatus
     * @param recordIds
     *
     * @return 1:成功 2：含有非待审核记录 3：数据库错误
     */
    @Transactional
    public Integer messageStatusChange(Integer messageStatus, List<Long> recordIds) {
        JLog.info("messageStatusChange service recordIds size=" + recordIds.size() + " " +
                "messageStatus=" + messageStatus);

        // 新状态对应的之前在数据库内的数据
        List<Long> recordsInDB = new ArrayList<>();
        if (messageStatus.equals(MessageRecord.MessageStatus.AUDIT_PASS) ||
                messageStatus.equals(MessageRecord.MessageStatus.AUDIT_FAILD)) {
            // 审核通过/不通过，查找需要等待审核，messageStatus=1的记录
            recordsInDB = mrm.findByMessageStatus(MessageRecord.MessageStatus.WAIT_AUDIT);
        } else if (messageStatus.equals(MessageRecord.MessageStatus.RECEIVED)) {
            // 审核通过消息已送达，查找审核通过记录
            recordsInDB = mrm.findByMessageStatus(MessageRecord.MessageStatus.AUDIT_PASS);
        } else if (messageStatus.equals(MessageRecord.MessageStatus.UNPASS_RESULT_RECEIVED)) {
            // 审核未通过消息已送达，查找审核未通过记录
            recordsInDB = mrm.findByMessageStatus(MessageRecord.MessageStatus.AUDIT_FAILD);
        } else if (messageStatus.equals(MessageRecord.MessageStatus.READ)) {
            // 审核通过消息已读，查找审核通过消息已送达记录
            recordsInDB = mrm.findByMessageStatus(MessageRecord.MessageStatus.RECEIVED);
        } else if (messageStatus.equals(MessageRecord.MessageStatus.UNPASS_RESULT_READ)) {
            // 审核未通过消息已读，查找审核未通过消息已送达记录
            recordsInDB =
                    mrm.findByMessageStatus(MessageRecord.MessageStatus.UNPASS_RESULT_RECEIVED);
        }

        // 判断待要更改的记录id是否在所有待审核记录id的子集中
        if (!recordsInDB.containsAll(recordIds)) {
            JLog.error("illegal recordIds=" + Arrays.toString(recordIds.toArray()), 104291138);
            return 2;
        }
        Integer updateRet = mrm.batchUpdateMessageStatusByRecordId(messageStatus, recordIds,
                System.currentTimeMillis()/1000);
        // 批量更新失败，回滚事务，返回false
        if (!updateRet.equals(recordIds.size())) {
            JLog.error("db error batch update messageStatus failed updateRet=" + updateRet,
                    104291054);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 3;
        }
        return 1;
    }

    /**
     * 给出接收人的id，查找出所有发送给他的消息
     *
     * @param receiverId
     *
     * @return
     */
    public List<WaitReadMsgs> pullMsgs(Long receiverId) {
        JLog.info("pullMsgs service receiverId=" + receiverId);
        List<WaitReadMsgs> waitReadMsgs;
        try {
            waitReadMsgs = mrm.findWaitReadMsgs(receiverId);
        } catch (Exception e) {
            JLog.error("pullMsgs db error receiverId=" + receiverId + " errMsg=" + e.getMessage()
                    , 104302313);
            return null;
        }
        return waitReadMsgs;
    }

    /**
     * 拉取审核未通过的消息
     *
     * @param spokeId
     *
     * @return
     */
    public List<WaitReadMsgDetail> pullUnpassMsgs(Long spokeId) {
        JLog.info("pullUnpassMsgs service spokeId=" + spokeId);
        List<WaitReadMsgDetail> unpassMsgs;
        try {
            unpassMsgs = mrm.findUnpassMsgs(spokeId);
        } catch (Exception e) {
            JLog.error("pullUnpassMsgs db error spokeId=" + spokeId + " errMsg=" + e.getMessage()
                    , 104311522);
            return null;
        }
        return unpassMsgs;
    }
}
