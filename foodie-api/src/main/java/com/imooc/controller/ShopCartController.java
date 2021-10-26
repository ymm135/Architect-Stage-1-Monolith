package com.imooc.controller;

import com.imooc.common.Common;
import com.imooc.pojo.bo.ShopCartBO;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车接口", tags = "购物车相关API接口")
@RestController
@RequestMapping("shopcart")
public class ShopCartController {

    private final static Logger logger = LoggerFactory.getLogger(ShopCartController.class);


    @Autowired
    private RedisOperator redisOperator;


    @ApiOperation(value = "增加商品到购物车", httpMethod = "POST", tags = "增加商品到购物车")
    @PostMapping("/add")
    public IMOOCJSONResult add(
            @RequestParam("userId") String userId,
            @RequestBody ShopCartBO shopCartBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (StringUtils.isEmpty(userId)) {
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        logger.info("userId=" + userId + "  ,shopcart=" + shopCartBO);

        // 等到Redis分布式学习之后，再去完善。用户登录之后，添加商品至购物车，并且会同步到后端!!
        //如果购物车中已经存在相同的商品，商品的数量需要累加
        String RESIS_KEY_SHOPCART = Common.SHOPCART + ":" + userId;
        String shopcartItemsStr = redisOperator.get(RESIS_KEY_SHOPCART);
        List<ShopCartBO> shopCartBOList = null;

        if (!StringUtils.isBlank(shopcartItemsStr)) {
            shopCartBOList = JsonUtils.jsonToList(shopcartItemsStr, ShopCartBO.class);
            boolean isExist = false;

            for (ShopCartBO shopCartItem : shopCartBOList) {
                if (shopCartItem.getSpecId().equals(shopCartBO.getSpecId())) {
                    isExist = true;

                    Integer buyCounts = shopCartItem.getBuyCounts();
                    shopCartItem.setBuyCounts(buyCounts + shopCartItem.getBuyCounts());
                    break;
                }
            }

            if (!isExist) {
                shopCartBOList.add(shopCartBO);
            }

        } else {
            shopCartBOList = new ArrayList<>();
            shopCartBOList.add(shopCartBO);
        }

        if(shopCartBOList != null && shopCartBOList.size() > 0) {
            redisOperator.set(RESIS_KEY_SHOPCART, JsonUtils.objectToJson(shopCartBOList));
        }

        return IMOOCJSONResult.ok(shopCartBOList);
    }

    @ApiOperation(value = "删除购物车商品", httpMethod = "POST", tags = "删除购物车商品")
    @PostMapping("/del")
    public IMOOCJSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (StringUtils.isEmpty(userId)) {
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        logger.info("userId=" + userId + "  ,itemSpecId=" + itemSpecId);

        // 等到Redis分布式学习之后，再去完善。需要删除Redis中商品的信息
        String RESIS_KEY_SHOPCART = Common.SHOPCART + ":" + userId;
        String shopcartItemsStr = redisOperator.get(RESIS_KEY_SHOPCART);
        List<ShopCartBO> shopCartBOList = null;

        if (!StringUtils.isBlank(shopcartItemsStr)) {
            shopCartBOList = JsonUtils.jsonToList(shopcartItemsStr, ShopCartBO.class);
            for (ShopCartBO shopCartItem : shopCartBOList) {
                if (shopCartItem.getSpecId().equals(itemSpecId)) {
                    shopCartBOList.remove(shopCartItem);
                    break;
                }
            }
        }

        if(shopCartBOList != null && shopCartBOList.size() > 0) {
            redisOperator.set(RESIS_KEY_SHOPCART, JsonUtils.objectToJson(shopCartBOList));
        }else {
            redisOperator.set(RESIS_KEY_SHOPCART, "");
        }


        return IMOOCJSONResult.ok(shopCartBOList);
    }

}
