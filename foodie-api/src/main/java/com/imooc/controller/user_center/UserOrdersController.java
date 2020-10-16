package com.imooc.controller.user_center;

import com.imooc.common.Common;
import com.imooc.pojo.vo.user_center.OrderStatusCountsVO;
import com.imooc.service.user_center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户订单接口", tags = "用户订单接口")
@RestController
@RequestMapping("myorders")
public class UserOrdersController {

    private final static Logger logger = LoggerFactory.getLogger(UserOrdersController.class);

    @Autowired
    private MyOrderService myOrderService;

    @ApiOperation(value = "获取用户订单信息", httpMethod = "POST", tags = "获取用户订单信息")
    @RequestMapping("/query")
    public IMOOCJSONResult query(
            @RequestParam String userId, @RequestParam Integer orderStatus,
            @RequestParam Integer page, @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = Common.DEFAULT_PAGESIZE;
        }

        PagedGridResult pagedGridResult = myOrderService.queryOrders(userId, orderStatus, page, pageSize);

        return IMOOCJSONResult.ok(pagedGridResult);
    }

    @ApiOperation(value = "用户确认收货", httpMethod = "POST", tags = "用户确认收货")
    @PostMapping("/confirmReceive")
    public IMOOCJSONResult confirmReceive(
            @RequestParam String userId, @RequestParam String orderId) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        IMOOCJSONResult checkRes = myOrderService.checkUserAndOrder(userId, orderId);
        if (!checkRes.isOK()) {
            return checkRes;
        }

        boolean confirmResult = myOrderService.confirmReceive(userId, orderId);
        return confirmResult ? IMOOCJSONResult.ok() : IMOOCJSONResult.errorMsg("确认收货失败");
    }


    @ApiOperation(value = "用户订单删除", httpMethod = "POST", tags = "用户订单删除")
    @PostMapping("/delete")
    public IMOOCJSONResult delete(
            @RequestParam String userId, @RequestParam String orderId) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        IMOOCJSONResult checkRes = myOrderService.checkUserAndOrder(userId, orderId);
        if (!checkRes.isOK()) {
            return checkRes;
        }

        boolean confirmResult = myOrderService.delete(userId, orderId);
        return confirmResult ? IMOOCJSONResult.ok() : IMOOCJSONResult.errorMsg("删除订单失败!!");
    }

    @ApiOperation(value = "用户订单状态", httpMethod = "POST", tags = "用户订单状态")
    @PostMapping("statusCounts")
    public IMOOCJSONResult orderStatusCounts(@RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        OrderStatusCountsVO userOrderCount = myOrderService.getUserOrderCount(userId);
        return IMOOCJSONResult.ok(userOrderCount);
    }

    @ApiOperation(value = "用户订单动向", httpMethod = "POST", tags = "用户订单动向")
    @PostMapping("trend")
    public IMOOCJSONResult orderTrend(@RequestParam String userId,
                                      @RequestParam Integer page, @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户ID不能为空!");
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = Common.DEFAULT_PAGESIZE;
        }

        PagedGridResult userOrderGrid = myOrderService.getUserOrderTrend(userId, page, pageSize);
        return IMOOCJSONResult.ok(userOrderGrid);
    }
}
