package com.gaofeng.prisonim.beans.msg;

import com.gaofeng.prisonim.beans.common.BaseReq;

/**
 * @Author: gaofeng
 * @Date: 2018-08-30
 * @Description:
 */
public class PullReq extends BaseReq {
    private Long receiverId;

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public String toString() {
        return "PullReq{" +
                "receiverId=" + receiverId +
                '}';
    }
}
