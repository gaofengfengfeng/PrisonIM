package com.gaofeng.prisonim.beans.msg;

import com.gaofeng.prisonim.beans.common.BaseReq;

/**
 * @Author: gaofeng
 * @Date: 2018-08-31
 * @Description:
 */
public class PullUnpassReq extends BaseReq {
    private Long spokeId;

    public Long getSpokeId() {
        return spokeId;
    }

    public void setSpokeId(Long spokeId) {
        this.spokeId = spokeId;
    }

    @Override
    public String toString() {
        return "PullUnpassReq{" +
                "spokeId=" + spokeId +
                '}';
    }
}
