package com.imooc.controller.user_center;

import com.imooc.common.Common;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.user_center.OrderItemsCommentsBO;
import com.imooc.service.user_center.CommentsService;
import com.imooc.service.user_center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "用户中心评价接口", tags = "用户中心评价接口")
@RestController
@RequestMapping("mycomments")
public class UserCommentsController {

    private final static Logger logger = LoggerFactory.getLogger(UserCommentsController.class);

    @Autowired
    private MyOrderService myOrderService;

    @Autowired
    private CommentsService commentsService;

    @ApiOperation(value = "获取用户评论的商品信息", httpMethod = "POST", tags = "获取用户评论的商品信息")
    @PostMapping("/pending")
    public IMOOCJSONResult pending(
            @RequestParam String userId, @RequestParam String orderId){

        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        IMOOCJSONResult checkRes = myOrderService.checkUserAndOrder(userId, orderId);
        if(!checkRes.isOK()){
            return checkRes;
        }

        //查看订单是否评论过
        Orders orders = (Orders) checkRes.getData();
        if(orders.getIsComment() == YesOrNo.YES.type){
            return IMOOCJSONResult.errorMsg("该订单已经评过了!");
        }


        List<OrderItems> orderItems = commentsService.queryPendingComments(orderId);

        return IMOOCJSONResult.ok(orderItems);
    }

    @ApiOperation(value = "保存用户评论", httpMethod = "POST", tags = "保存用户评论")
    @PostMapping("/saveList")
    public IMOOCJSONResult saveList(@RequestParam String userId,
                                    @RequestParam String orderId,
                                    @RequestBody List<OrderItemsCommentsBO> commentsList){

        logger.info("saveList=" + commentsList);

        if(commentsList == null || commentsList.isEmpty()){
            return IMOOCJSONResult.errorMsg("评论数据不能为空!!");
        }

        commentsService.saveComments(userId, orderId, commentsList);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "查询用户评论", httpMethod = "POST", tags = "查询用户评论")
    @PostMapping("/query")
    public IMOOCJSONResult query(@RequestParam String userId,
                                 @RequestParam Integer page,
                                 @RequestParam Integer pageSize){

        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        if(page == null){
            page = 1;
        }

        if(pageSize == null){
            pageSize = Common.DEFAULT_PAGESIZE;
        }

        PagedGridResult pagedGridResult = commentsService.queryUserComments(userId, page, pageSize);

        return IMOOCJSONResult.ok(pagedGridResult);
    }



}
