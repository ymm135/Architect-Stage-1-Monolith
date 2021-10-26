package com.imooc.controller.interceptor;

import com.imooc.common.Common;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 用户拦截器
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(UserTokenInterceptor.class);

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 请求之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        /**
         * 'headerUserId': userInfo.id,
         * 'headerUserToken': userInfo.userUniqueToken
         */

        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        logger.warn("userId=" + userId + ",userToken=" + userToken + ",preHandle --" + handler);

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(userToken)) {
            returnErrorDdata(response, JsonUtils.objectToJson(IMOOCJSONResult.errorMsg("请先登录!")));
        }else {
            //读取redis用户信息
            String userUUID = redisOperator.get(Common.REDIS_USER_TOKEN + ":" + userId);
            if(StringUtils.isBlank(userUUID)){
                returnErrorDdata(response, JsonUtils.objectToJson(IMOOCJSONResult.errorMsg("请先登录!")));
            }else {
                if(!userUUID.equals(userToken)){
                    returnErrorDdata(response, JsonUtils.objectToJson(IMOOCJSONResult.errorMsg("已经异地登录，请重新登录!")));
                }
            }
        }

        return true;
    }

    private String returnErrorDdata(HttpServletResponse response, String data) {
        OutputStream outputStream = null;
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");

        try {
            outputStream = response.getOutputStream();
            outputStream.write(data.getBytes("utf-8"));

            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 请求controller之后，渲染视图之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求controller之后，渲染视图之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
