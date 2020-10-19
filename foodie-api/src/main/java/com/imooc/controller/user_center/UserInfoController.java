package com.imooc.controller.user_center;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.user_center.UserInfoBO;
import com.imooc.resources.FileUploadResources;
import com.imooc.service.user_center.UserCenterService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户信息接口", tags = "用户信息接口")
@RestController
@RequestMapping("userInfo")
public class UserInfoController {

    private final static Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private FileUploadResources fileUploadResources;

    @ApiOperation(value = "修改用户信息", httpMethod = "POST", tags = "修改用户信息")
    @PostMapping("/update")
    public IMOOCJSONResult update(
            @RequestParam String userId, @RequestBody @Valid UserInfoBO userInfoBO,
            HttpServletRequest request, HttpServletResponse response, BindingResult bindingResult) {

        Map<String, String> resultFromBinding = getResultFromBinding(bindingResult);
        if (bindingResult.hasErrors()) {
            return IMOOCJSONResult.errorMap(resultFromBinding);
        }

        Users users = userCenterService.updateUserInfo(userId, userInfoBO);
        setNull(users);

        //更新到Cookie中
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(users), true);

        //TODO 整合Redis之后，需要增加token

        return IMOOCJSONResult.ok(users);
    }

    private void setNull(Users users) {
        users.setPassword(null);
        users.setCreatedTime(null);
        users.setUpdatedTime(null);
    }

    private Map<String, String> getResultFromBinding(BindingResult bindingResult) {
        Map<String, String> errorMaps = new HashMap<>();

        List<FieldError> allErrors = bindingResult.getFieldErrors();
        for (FieldError error : allErrors) {
            String field = error.getField();
            String defaultMessage = error.getDefaultMessage();

            errorMaps.put(field, defaultMessage);
        }

        return errorMaps;
    }

    @ApiOperation(value = "修改用户头像", httpMethod = "POST", tags = "修改用户头像")
    @PostMapping("/uploadFace")
    public IMOOCJSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "用户上传的图像地址", required = true)
            @RequestBody MultipartFile file,
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.info("uploadFace=" + userId + ",userFaceFile=" + file);

        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isNoneBlank(originalFilename)) {
                String[] paths = originalFilename.split("\\.");
                if (paths.length > 1) {
                    String fileType = paths[paths.length - 1];

                    if(!fileType.equalsIgnoreCase("png") &&
                            !fileType.equalsIgnoreCase("jpg") &&
                            !fileType.equalsIgnoreCase("jpeg")){
                            return IMOOCJSONResult.errorMsg("图片格式不正确!");
                    }

                    String fileNewName = "face-" + userId + "." + fileType;

                    String fileNewPath = fileUploadResources.getSavePath() + File.separator + fileNewName;

                    File userOutputFile = new File(fileNewPath);
                    if (userOutputFile.getParentFile() != null) {
                        userOutputFile.getParentFile().mkdirs();
                    }

                    FileOutputStream fos = null;
                    if (!userOutputFile.exists()) {
                        userOutputFile.createNewFile();
                    } else {
                        userOutputFile.delete();
                    }

                    try {
                        fos = new FileOutputStream(userOutputFile);

                        InputStream is = file.getInputStream();
                        IOUtils.copy(is, fos);

                        //由于服务器存在缓存，需要增加时间戳？？
                        String userFaceUrl = fileUploadResources.getImgUrl() + File.separator + fileNewName
                                + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);

                        //更新用户头像到数据库
                        Users users = userCenterService.updateUserFace(userId, userFaceUrl);

                        setNull(users);

                        //更新到Cookie中
                        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(users), true);

                        //TODO 整合Redis之后，需要增加token

                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else {
            return IMOOCJSONResult.errorMsg("文件不能为空!");
        }


        return IMOOCJSONResult.ok();
    }

}
