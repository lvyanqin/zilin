/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class OrderUtil {
    
    /**
     * 支付方式，支付宝，微信，银联
     */
    public static Map<String, Integer> PAYWAY = new HashMap(){
        {
            put("alipay", 1);
            put("weixin", 2);
            put("unionpay", 3);
        }
    };
    public static final Integer PAYWAY_ALIPAY = 1;
    public static final Integer PAYWAY_WEIXIN = 2;
    public static final Integer PAYWAY_UNIONPAY = 3;
    
    /**
     * 订单类型,购卡
     */
    public static final String ORDER_TYPE_CARD = "card";
    
    /**
     * 所有的订单类型，用来过滤判断是否合法
     */
    public static final String ORDER_TYPE_ALL = ORDER_TYPE_CARD;
    
    /**
     * 订单号 前缀，卡号
     */
    public static final String ORDERNO_PRE_CARD = "001";
    
    /**
     * 生成订单号
     * 
     * @param orderType
     * @return 
     */
    public static final String generateOrderNo(String orderType){
        StringBuilder orderNo = new StringBuilder();
        if(orderType.equals(ORDER_TYPE_CARD)){
            orderNo.append(ORDERNO_PRE_CARD);
        }
        orderNo.append(System.currentTimeMillis());
        return orderNo.toString();
    }
    
}
