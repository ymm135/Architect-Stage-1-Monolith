package com.imooc.service.impl.user_center;

import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.user_center.UserInfoBO;
import com.imooc.service.user_center.UserCenterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserCenterServiceImpl implements UserCenterService {

    @Autowired
    private UsersMapper usersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfos(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        users.setPassword(null);

        return users;
    }

    @Override
    public Users updateUserInfo(String userId, UserInfoBO userInfoBO) {
        Users updateUsers = new Users();
        BeanUtils.copyProperties(userInfoBO, updateUsers);

        updateUsers.setId(userId);
        updateUsers.setUpdatedTime(new Date());

        int result = usersMapper.updateByPrimaryKeySelective(updateUsers);

        return queryUserInfos(userId);
    }

    @Override
    public Users updateUserFace(String userId, String userFacePath) {
        Users userFace = new Users();
        userFace.setId(userId);
        userFace.setFace(userFacePath);

        int res = usersMapper.updateByPrimaryKeySelective(userFace);

        return queryUserInfos(userId);
    }
}
