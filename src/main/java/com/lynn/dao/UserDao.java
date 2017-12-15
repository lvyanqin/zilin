/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lynn.dao;

import com.lynn.bean.UserBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @author Administrator
 */
public interface UserDao {
    
    @Select("SELECT * FROM zilin_user_login WHERE id = #{userId} and state = 1 ")
    public UserBean getUserById(@Param("userId")Integer userId);
    
}
