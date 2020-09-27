package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import org.apache.catalina.User;

public interface UserService {

    public boolean queryUsernameIsExist(String username);
    public Users addUser(UserBO userBO);
    public Users login(String username,String password);
}
