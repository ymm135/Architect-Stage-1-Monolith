package com.imooc.controller;

import com.imooc.pojo.bo.ShopCartBO;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "购物车接口", tags = "购物车相关API接口")
@RestController
@RequestMapping("shopcart")
public class ShopCartController {

    private final static Logger logger = LoggerFactory.getLogger(ShopCartController.class);

    @ApiOperation(value = "增加商品到购物车", httpMethod = "POST", tags = "增加商品到购物车")
    @PostMapping("/add")
    public IMOOCJSONResult add(
            @RequestParam("userId") String userId,
            @RequestBody ShopCartBO shopCartBO,
            HttpServletRequest request,
            HttpServletResponse response){

        if(StringUtils.isEmpty(userId)){
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        logger.info("userId=" + userId + "  ,shopcart=" + shopCartBO);

        //TODO 等到Redis分布式学习之后，再去完善。用户登录之后，添加商品至购物车，并且会同步到后端!!

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "删除购物车商品", httpMethod = "POST", tags = "删除购物车商品")
    @PostMapping("/del")
    public IMOOCJSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response){

        if(StringUtils.isEmpty(userId)){
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        logger.info("userId=" + userId + "  ,itemSpecId=" + itemSpecId);

        //TODO 等到Redis分布式学习之后，再去完善。需要删除Redis中商品的信息

        return IMOOCJSONResult.ok();
    }

}
