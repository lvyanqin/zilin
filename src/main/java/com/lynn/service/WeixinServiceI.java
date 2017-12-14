/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.service;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Administrator
 */
public interface WeixinServiceI {
    
    /**
     * 微信支付回调通知接口
     *
     * @param request
     * @return
     */
    public String WeixinNotify(HttpServletRequest request);
    
}
