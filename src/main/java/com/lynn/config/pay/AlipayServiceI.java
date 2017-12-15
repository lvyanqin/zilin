/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.config.pay;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Administrator
 */
public interface AlipayServiceI {
    
    /**
     * PC端跳转支付宝网站进行支付
     *
     * @param orderNumber
     * @param price
     * @return
     */
    public String alipayPCWebCreateUrl(String orderNumber, Double price);
    
    /**
     * PC端调转支付宝支付成功后验证（即时支付接口）
     * 
     * @param request
     * @return 
     */
    public boolean alipayPCWebCheckURLOrder(HttpServletRequest request);
    
}
