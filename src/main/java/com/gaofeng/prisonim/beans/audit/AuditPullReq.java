package com.gaofeng.prisonim.beans.audit;

import com.gaofeng.prisonim.beans.common.BaseReq;

/**
 * @Author: gaofeng
 * @Date: 2018-08-26
 * @Description:
 */
public class AuditPullReq extends BaseReq {
    private Long policeId;

    public Long getPoliceId() {
        return policeId;
    }

    public void setPoliceId(Long policeId) {
        this.policeId = policeId;
    }

    @Override
    public String toString() {
        return "AuditPullReq{" +
                "policeId=" + policeId +
                '}';
    }
}
