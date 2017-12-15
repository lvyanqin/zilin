/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.service;

import com.lynn.bean.UserBean;
import com.lynn.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Administrator
 */
@Service
public class UserServiceImp implements UserServiceI{
    
    @Autowired
    private UserDao userDao;
    
    public UserBean getUserById(Integer userId){
        UserBean user = userDao.getUserById(userId);
        return user;
    }
    
}
