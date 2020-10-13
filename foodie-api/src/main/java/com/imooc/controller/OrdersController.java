package com.imooc.controller;

import com.imooc.common.Common;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.OrderService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "订单相关接口", tags = "订单相关接口")
@RestController
@RequestMapping("orders")
public class OrdersController {

    private final static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "创建订单接口", httpMethod = "POST", tags = "创建订单接口")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO, HttpServletRequest request, HttpServletResponse response) {

        logger.info(JsonUtils.objectToJson(submitOrderBO));

        //判断支付方式
        Integer payMethod = submitOrderBO.getPayMethod();
        if (payMethod != PayMethod.ALIPAY.type && payMethod != PayMethod.WEIXIN.type) {
            return IMOOCJSONResult.errorMsg("支付方式不支持!");
        }

        //1.创建订单
        OrderVO order = orderService.createOrder(submitOrderBO);
        MerchantOrdersVO merchantOrdersVO = order.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(Common.PAY_RETURN_URL);

        //TODO 方便测试，支付金额为1分钱
        merchantOrdersVO.setAmount(1);

        //2.移除购物车已提交的商品
        //TODO 整合Redis之后，再去完善购物车

        //清除cookie
        //CookieUtils.setCookie(request, response, "shopcart", "");

        //3.向支付中心提交当前订单，用于保存支付中心的订单信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //授权
        headers.add("imoocUserId", Common.USER_NAME);
        headers.add("password", Common.USER_PWD);

        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<IMOOCJSONResult> resultResponseEntity =
                restTemplate.postForEntity(Common.PAYMENT_URL, entity, IMOOCJSONResult.class);

        IMOOCJSONResult paymentResult = resultResponseEntity.getBody();
        if(!paymentResult.isOK()){
            return IMOOCJSONResult.errorMsg("创建订单失败!");
        }

        return IMOOCJSONResult.ok(order.getOrderId());
    }


    @ApiOperation(value = "获取订单信息", httpMethod = "POST", tags = "获取订单信息")
    @PostMapping("/getPaidOrderInfo")
    public IMOOCJSONResult getPaidOrderInfo(@RequestParam String orderId) {

        OrderStatus paidOrderInfo = orderService.getPaidOrderInfo(orderId);
        return IMOOCJSONResult.ok(paidOrderInfo);
    }

    @ApiOperation(value = "获取订单信息", httpMethod = "POST", tags = "获取订单信息")
    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(@RequestParam String merchantOrderId) {

        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }


}
