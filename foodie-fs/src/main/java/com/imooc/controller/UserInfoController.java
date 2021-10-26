package com.imooc.controller;

import com.imooc.common.Common;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UserVO;
import com.imooc.resources.FileUploadResources;
import com.imooc.service.FdfsService;
import com.imooc.service.user_center.UserCenterService;
import com.imooc.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.UUID;

@Api(value = "用户信息接口", tags = "用户信息接口")
@RestController
@RequestMapping("fdfs")
public class UserInfoController {

    private final static Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private FdfsService fdfsService;

    @Autowired
    private FileUploadResources fileUploadResources;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/hello")
    public String hello(){
        return "Fdfs Controller!";
    }

    @ApiOperation(value = "修改用户头像", httpMethod = "POST", tags = "修改用户头像")
    @PostMapping("/uploadFace")
    public IMOOCJSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "用户上传的图像地址", required = true)
            @RequestBody MultipartFile file,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("uploadFace=" + userId + ",userFaceFile=" + file);
        String uploadPath = null;
        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isNoneBlank(originalFilename)) {
                String[] paths = originalFilename.split("\\.");
                if (paths.length > 1) {
                    String fileType = paths[paths.length - 1];

                    if (!fileType.equalsIgnoreCase("png") &&
                            !fileType.equalsIgnoreCase("jpg") &&
                            !fileType.equalsIgnoreCase("jpeg")) {
                        return IMOOCJSONResult.errorMsg("图片格式不正确!");
                    }


                    uploadPath = fdfsService.upload(file, fileType);
                    logger.info("头像文件路径:" + uploadPath);

                } else {
                    return IMOOCJSONResult.errorMsg("文件不能为空!");
                }
            }
        }

        if(StringUtils.isNotBlank(uploadPath)){
            //保存数据
            //由于服务器存在缓存，需要增加时间戳？？
            String userFaceUrl = fileUploadResources.getImgUrl() + File.separator + uploadPath
                    + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);

            //更新用户头像到数据库
            Users users = userCenterService.updateUserFace(userId, userFaceUrl);

            // 整合Redis之后，需要增加token
            UserVO userVO = getUserVO(users);

            //更新到Cookie中
            CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userVO), true);

        }else {
            return IMOOCJSONResult.errorMsg("头像上传失败!");
        }

        return IMOOCJSONResult.ok();
    }


    private UserVO getUserVO(Users users) {
        String uuid = UUID.randomUUID().toString().trim();
        redisOperator.set(Common.REDIS_USER_TOKEN + ":" + users.getId(), uuid);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(users, userVO);
        userVO.setUserUniqueToken(uuid);
        return userVO;
    }
}
