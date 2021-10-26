package com.imooc.service.user_center;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.user_center.UserInfoBO;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserCenterService {
    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    Users queryUserInfos(String userId);

    /**
     *
     * @param userId
     * @param userInfoBO
     * @return
     */
    Users updateUserInfo(String userId, UserInfoBO userInfoBO);


    /**
     * 更新用户头像
     * @param userId
     * @param userFacePath
     * @return
     */
    Users updateUserFace(String userId, String userFacePath);

}
