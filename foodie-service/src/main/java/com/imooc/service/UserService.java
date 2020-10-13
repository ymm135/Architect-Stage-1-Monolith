package com.imooc.service;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import org.apache.catalina.User;

public interface UserService {

    boolean queryUsernameIsExist(String username);
    Users addUser(UserBO userBO);
    Users login(String username,String password);

}
