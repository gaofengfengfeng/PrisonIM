package com.gaofeng.prisonim.beans.msg;

import com.gaofeng.prisonim.beans.common.BaseReq;

/**
 * @Author: gaofeng
 * @Date: 2018-08-26
 * @Description:
 */
public class SendReq extends BaseReq {
    private Long spokeId;
    private Long receiverId;
    private Integer sendType;
    private Integer messageType;
    private String content;

    public Long getSpokeId() {
        return spokeId;
    }

    public void setSpokeId(Long spokeId) {
        this.spokeId = spokeId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SendReq{" +
                "spokeId=" + spokeId +
                ", receiverId=" + receiverId +
                ", sendType=" + sendType +
                ", messageType=" + messageType +
                ", content='" + content + '\'' +
                '}';
    }
}
