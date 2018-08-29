package com.gaofeng.prisonim.controller;

import com.didi.meta.javalib.JLog;
import com.didi.meta.javalib.JResponse;
import com.gaofeng.prisonim.beans.msg.SendReq;
import com.gaofeng.prisonim.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
}
