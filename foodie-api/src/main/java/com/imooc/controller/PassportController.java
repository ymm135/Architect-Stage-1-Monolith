package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ShopCartBO;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import com.imooc.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value = "注册登录", tags = "用于注册登录的接口")
@RestController
@RequestMapping(path = "passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "判断用户是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username) {

        //1.判断用户名不能为空
        if (StringUtils.isEmpty(username)) {
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        //2.查找用于名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);

        if (isExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }

        //3.请求成功
        return IMOOCJSONResult.ok();

    }

    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        if (StringUtils.isEmpty(username) ||
                StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(confirmPassword)
        ) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("密码不能少于6位");
        }

        if (!password.equals(confirmPassword)) {
            return IMOOCJSONResult.errorMsg("两次输入的密码不一致");
        }

        //校验通过，注册用户
        Users users = userService.addUser(userBO);
        setNull(users);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(users), true);

        return IMOOCJSONResult.ok(users);
    }

    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody Map params, HttpServletRequest request, HttpServletResponse response) {


        String userName = (String) params.get("username");
        String password = (String) params.get("password");

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        Users users = null;
        try {
            users = userService.login(userName, MD5Utils.getMD5Str(password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (users == null) {
            return IMOOCJSONResult.errorMsg("用户名或密码不正确");
        }

        setNull(users);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(users), true);

        //TODO 生成用户TOKEN，存入Redis会话，
        // 同步购物车数据
        syncCookieAndRedisShopcartData(request, response, users.getId());

        return IMOOCJSONResult.ok(users);
    }

    /**
     * 同步Cookie与redis中购物车的数据
     *
     * 如果redis为空
     *          cookie也为空，不做处理
     *          cookie不为空，在redis中存储cookie的数据
     */
    private void syncCookieAndRedisShopcartData(HttpServletRequest request, HttpServletResponse response, String userId){
        final String cookieName = "shopcart";
        final String shopcart_key = "shopcart:" + userId;
        String shopcartRedisStr = redisOperator.get(shopcart_key);
        String cookieValue = CookieUtils.getCookieValue(request, cookieName, true);


        if(StringUtils.isNotBlank(shopcartRedisStr)){
            if(!StringUtils.isBlank(cookieValue)){
                List<ShopCartBO> shopCartBOCookieList = JsonUtils.jsonToList(cookieValue, ShopCartBO.class);
                List<ShopCartBO> shopCartBORedisList = JsonUtils.jsonToList(shopcartRedisStr, ShopCartBO.class);
                List<ShopCartBO> toBeDeletingList = null;

                for (ShopCartBO shopCartRedisBO : shopCartBORedisList){
                    for (ShopCartBO shopCartCookieBO : shopCartBOCookieList){
                        if(shopCartRedisBO.getSpecId().equals(shopCartCookieBO.getSpecId())){
                            if(toBeDeletingList == null){
                                toBeDeletingList = new ArrayList<>();
                            }

                            //相同商品
                            shopCartRedisBO.setBuyCounts(shopCartCookieBO.getBuyCounts());
                            toBeDeletingList.add(shopCartRedisBO);
                        }
                    }
                }

                if(toBeDeletingList != null){
                    shopCartBORedisList.removeAll(toBeDeletingList);
                    shopCartBORedisList.addAll(shopCartBOCookieList);

                    String shopcartRes = JsonUtils.objectToJson(shopCartBORedisList);

                    CookieUtils.setCookie(request, response, cookieName, shopcartRes, true);
                    redisOperator.set(shopcart_key, shopcartRes);
                }

            }else {
                //redis不为空，cookie为空
                CookieUtils.setCookie(request, response, cookieName,shopcartRedisStr,true);
            }

        }else { //redis 数据为空
            if(!StringUtils.isBlank(cookieValue)){
                redisOperator.set(shopcart_key, cookieValue);
            }
        }

    }

    private void setNull(Users users) {
        users.setPassword(null);
        users.setCreatedTime(null);
        users.setUpdatedTime(null);
        users.setBirthday(null);
        users.setRealname(null);
    }

    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {

        //退出时，需要删除uerId
        CookieUtils.deleteCookie(request, response, "user");


        //TODO 用户退出，清空购物车
        // 分布式系统中，清除用户数据
        CookieUtils.deleteCookie(request, response, "shopcart");

        return IMOOCJSONResult.ok();
    }

}
