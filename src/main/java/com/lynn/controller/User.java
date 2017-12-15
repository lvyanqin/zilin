/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.controller;

import com.lynn.service.UserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Administrator
 */
@Controller
public class User {
    
    @Autowired
    private UserServiceI userService;
    
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ModelAndView init(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("user", userService.getUserById(1));
        mv.setViewName("user");
        return mv;
    }
    
}
