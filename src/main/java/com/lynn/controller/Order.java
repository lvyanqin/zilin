/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.controller;

import com.lynn.bean.OrderBean;
import com.lynn.util.OrderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Administrator
 */
@Controller
public class Order {
    
    @RequestMapping("/order/wirte")
    public ModelAndView writePage(String orderType, String payWay){
        ModelAndView mv = new ModelAndView();
        if(StringUtils.isBlank(orderType) || !OrderUtil.ORDER_TYPE_ALL.contains(orderType)){
            mv.setViewName("404");
            return mv;
        }
        OrderBean order = new OrderBean();
        order.setOrderno(OrderUtil.generateOrderNo(orderType));
//        order.setPayway();
//        order.setUserid();
        mv.setViewName("/pay/write");
        return mv;
    }
    
}
