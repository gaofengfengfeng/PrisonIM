package com.gaofeng.beans.common;

import com.didi.meta.javalib.JRequest;

/**
 * @Author: gaofeng
 * @Date: 2018-08-23
 * @Description:
 */
public class BaseReq extends JRequest {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "BaseReq{" +
                "token='" + token + '\'' +
                '}';
    }
}
