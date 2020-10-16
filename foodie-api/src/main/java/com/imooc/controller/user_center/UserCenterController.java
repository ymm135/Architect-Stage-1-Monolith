package com.imooc.controller.user_center;

import com.imooc.pojo.Users;
import com.imooc.service.user_center.UserCenterService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@Api(value = "用户中心接口", tags = "用户中心接口")
@RestController
@RequestMapping("center")
public class UserCenterController {

    private final static Logger logger = LoggerFactory.getLogger(UserCenterController.class);

    @Autowired
    private UserCenterService userCenterService;

    @ApiOperation(value = "获取用户信息", httpMethod = "GET", tags = "获取用户信息")
    @GetMapping("/userInfo")
    public IMOOCJSONResult userInfo(
            @RequestParam String userId){

        Users users = userCenterService.queryUserInfos(userId);

        return IMOOCJSONResult.ok(users);
    }

}
