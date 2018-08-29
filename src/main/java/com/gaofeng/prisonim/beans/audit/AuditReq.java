package com.gaofeng.prisonim.beans.audit;

import com.didi.meta.javalib.JRequest;

import java.util.List;

/**
 * @Author: gaofeng
 * @Date: 2018-08-29
 * @Description: 审核通过和不通过请求
 */
public class AuditReq extends JRequest {
    private Long policeId;
    private List<Long> recordIds;

    public Long getPoliceId() {
        return policeId;
    }

    public void setPoliceId(Long policeId) {
        this.policeId = policeId;
    }

    public List<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }

    @Override
    public String toString() {
        return "AuditReq{" +
                "policeId=" + policeId +
                ", recordIds=" + recordIds +
                '}';
    }
}
