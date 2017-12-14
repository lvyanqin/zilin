/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.controller;

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
    public ModelAndView writePage(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/pay/write");
        return mv;
    }
    
}
