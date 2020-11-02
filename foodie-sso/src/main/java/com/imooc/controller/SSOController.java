package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UserVO;
import com.imooc.service.UserService;
import com.imooc.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Api(value = "单点登录", tags = "单点登录")
@Controller
public class SSOController {

    private final static Logger logger = LoggerFactory.getLogger(SSOController.class);

    private final String REDIS_KEY_USER_TOKEN = "redis_user_token";
    private final String REDIS_KEY_USER_TICKET = "redis_user_ticket";
    private final String REDIS_KEY_USER_TMP_TICKET = "redis_user_tmp_ticket";

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "登录", httpMethod = "GET", tags = "登录")
    @GetMapping("/login")
    public String login(String returnUrl, Model model,
                        HttpServletRequest request, HttpServletResponse response) {

        model.addAttribute("returnUrl", returnUrl);

        //TODO 后续校验是否登录
        //获取用户门票
        Cookie[] cookies = request.getCookies();
        String userTicket = null;

        if (cookies != null && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                logger.info("cookie domain=" + cookie.getDomain());

                if (REDIS_KEY_USER_TICKET.equals(cookie.getName())) {
                    userTicket = cookie.getValue();
                }
            }
        }

        if (StringUtils.isNotBlank(userTicket)) {
            String userId = redisOperator.get(REDIS_KEY_USER_TICKET + ":" + userTicket);
            if (StringUtils.isNotBlank(userId)) {
                //获取用户会话信息
                String userInfoStr = redisOperator.get(REDIS_KEY_USER_TOKEN + ":" + userId);
                if (StringUtils.isNotBlank(userInfoStr)) {
                    //临时门票
                    String tmpTicket = UUID.randomUUID().toString().trim();

                    try {
                        redisOperator.set(REDIS_KEY_USER_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket));
                        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(String username, String password,
                          String returnUrl, Model model,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (StringUtils.isBlank(username)) {
            model.addAttribute("errmsg", "用户名不能为空!");
            return "login";
        }

        if (StringUtils.isBlank(password)) {
            model.addAttribute("errmsg", "密码不能为空!");
            return "login";
        }

        Users users = userService.login(username, MD5Utils.getMD5Str(password));

        //全局会话
        if (users == null) {
            model.addAttribute("errmsg", "用户名或密码不正确!");

            return "login";
        }

        UserVO userVO = getUserVO(users);
        model.addAttribute("returnUrl", returnUrl);

        //全局门票
        String userTicket = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_KEY_USER_TICKET + ":" + userTicket, userVO.getId());

        //临时门票
        String tmpTicket = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_KEY_USER_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket), 600);

        //保存Cookie信息
        setCookie(REDIS_KEY_USER_TICKET, userTicket, response);


        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }

    private void setCookie(String key, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain("sso.com");
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    private void delCookie(String key, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);

        response.addCookie(cookie);
    }

    private UserVO getUserVO(Users users) {
        String uuid = UUID.randomUUID().toString().trim();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(users, userVO);
        userVO.setUserUniqueToken(uuid);

        redisOperator.set(REDIS_KEY_USER_TOKEN + ":" + users.getId(), JsonUtils.objectToJson(userVO));
        return userVO;
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public IMOOCJSONResult verifyTmpTicket(String tmpTicket, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //临时门票校验
        if (StringUtils.isBlank(tmpTicket)) {
            return IMOOCJSONResult.errorMsg("请携带临时门票!");
        }

        String tmpTicketVal = redisOperator.get(REDIS_KEY_USER_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicket) || tmpTicketVal == null || !tmpTicketVal.equals(MD5Utils.getMD5Str(tmpTicket))) {
            return IMOOCJSONResult.errorMsg("临时门票异常!");
        } else {
            redisOperator.del(REDIS_KEY_USER_TMP_TICKET + ":" + tmpTicket);
        }

        //获取用户门票
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return IMOOCJSONResult.errorMsg("无法获取cookie信息!");
        }

        String userTicket = null;
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            logger.info("cookie domain=" + cookie.getDomain());

            if (REDIS_KEY_USER_TICKET.equals(cookie.getName())) {
                userTicket = cookie.getValue();
            }
        }

        String userId = redisOperator.get(REDIS_KEY_USER_TICKET + ":" + userTicket);

        if (StringUtils.isBlank(userTicket) || StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户门票异常!");
        }

        //获取用户会话信息
        String userInfoStr = redisOperator.get(REDIS_KEY_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userInfoStr)) {
            return IMOOCJSONResult.errorMsg("用户信息异常!");
        }

        return IMOOCJSONResult.ok(JsonUtils.jsonToPojo(userInfoStr, UserVO.class));
    }

    @PostMapping("/logout")
    @ResponseBody
    public IMOOCJSONResult logout(String userId, HttpServletRequest request, HttpServletResponse response) {

        //清除用户Session信息
        redisOperator.del(REDIS_KEY_USER_TOKEN + ":" + userId);

        //清除全局门票
        String userTicket = getCookieValue(request, REDIS_KEY_USER_TICKET);
        if (StringUtils.isNotBlank(userTicket)) {
            redisOperator.del(REDIS_KEY_USER_TICKET + ":" + userTicket);
        }

        //删除cas cookie信息
        delCookie(REDIS_KEY_USER_TICKET, response);

        return IMOOCJSONResult.ok();
    }

    private String getCookieValue(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        String cookieVal = null;

        if (cookies != null && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (key.equals(cookie.getName())) {
                    cookieVal = cookie.getValue();
                }
            }
        }

        logger.info("getCookieValue val=" + cookieVal);
        return cookieVal;
    }


}
