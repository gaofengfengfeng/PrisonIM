package com.gaofeng.prisonim.beans.msg;

import com.gaofeng.prisonim.beans.common.BaseReq;

import java.util.List;

/**
 * @Author: gaofeng
 * @Date: 2018-08-31
 * @Description:
 */
public class MsgStatusChangeReq extends BaseReq {
    private List<RecordIdAndStatus> recordIdAndStatuses;

    public List<RecordIdAndStatus> getRecordIdAndStatuses() {
        return recordIdAndStatuses;
    }

    public void setRecordIdAndStatuses(List<RecordIdAndStatus> recordIdAndStatuses) {
        this.recordIdAndStatuses = recordIdAndStatuses;
    }

    public class RecordIdAndStatus {
        private Long recordId;
        private Integer messageStatus;

        public Long getRecordId() {
            return recordId;
        }

        public void setRecordId(Long recordId) {
            this.recordId = recordId;
        }

        public Integer getMessageStatus() {
            return messageStatus;
        }

        public void setMessageStatus(Integer messageStatus) {
            this.messageStatus = messageStatus;
        }

        @Override
        public String toString() {
            return "RecordIdAndStatus{" +
                    "recordId=" + recordId +
                    ", messageStatus=" + messageStatus +
                    '}';
        }
    }

}
