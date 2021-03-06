package com.imooc.controller;

import com.imooc.common.Common;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.ShopCartBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.OrderService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api(value = "订单相关接口", tags = "订单相关接口")
@RestController
@RequestMapping("orders")
public class OrdersController {

    private final static Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private RedissonClient redissonClient;

    @RequestMapping("/getOrderToken")
    public IMOOCJSONResult getOrderToken(HttpSession session){
        //防止重复提交，提供token
        String token = UUID.randomUUID().toString();

        //保存在redis中
        redisOperator.set("Order_Token_" + session.getId(), token, 600 * 1000); //10分钟有效期

        return IMOOCJSONResult.ok(token);
    }

    @ApiOperation(value = "创建订单接口", httpMethod = "POST", tags = "创建订单接口")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO, HttpServletRequest request, HttpServletResponse response) {

        logger.info(JsonUtils.objectToJson(submitOrderBO));
        String clientToken = submitOrderBO.getToken();

        if( StringUtils.isBlank(clientToken)){
            return IMOOCJSONResult.errorMsg("token不存在");
        }

        //获取token要加锁
        RLock rLock = redissonClient.getLock(clientToken);
        rLock.lock(5, TimeUnit.SECONDS);

        try {
            final String TOKEN_KEY = "Order_Token_" + request.getSession().getId();
            String token = redisOperator.get(TOKEN_KEY);
            if(!clientToken.equals(token)){
                throw new RuntimeException("token不匹配");
            }

            //redis删除token，就不用加锁了，但是会并发执行，同时进入这个方法，就需要加锁
            redisOperator.del(TOKEN_KEY);

        }finally {
            try {
                rLock.unlock();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //判断支付方式
        Integer payMethod = submitOrderBO.getPayMethod();
        if (payMethod != PayMethod.ALIPAY.type && payMethod != PayMethod.WEIXIN.type) {
            return IMOOCJSONResult.errorMsg("支付方式不支持!");
        }

        //1.创建订单
        IMOOCJSONResult orderRes = orderService.createOrder(submitOrderBO);
        if(!orderRes.isOK()){
            return orderRes;
        }

        OrderVO order = (OrderVO)orderRes.getData();
        MerchantOrdersVO merchantOrdersVO = order.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(Common.PAY_RETURN_URL);

        //TODO 方便测试，支付金额为1分钱
        merchantOrdersVO.setAmount(1);

        //2.移除购物车已提交的商品
        // 整合Redis之后，再去完善购物车
        // 清除购物车，如果需要多次测试，可以不清空！！

        List<ShopCartBO> toBeRemoveShopCartList = order.getToBeRemoveShopCartList();
        String RESIS_KEY_SHOPCART = "shopcart:" + submitOrderBO.getUserId();
        String shopcartItemsStr = redisOperator.get(RESIS_KEY_SHOPCART);
        List<ShopCartBO> shopCartBOList = null;

        if(!StringUtils.isBlank(shopcartItemsStr)){
            shopCartBOList = JsonUtils.jsonToList(shopcartItemsStr, ShopCartBO.class);
            shopCartBOList.removeAll(toBeRemoveShopCartList);
        }else {
            return IMOOCJSONResult.errorMsg("购物车信息为空!!");
        }

        String shopcartInfo = JsonUtils.objectToJson(shopCartBOList);
        redisOperator.set(RESIS_KEY_SHOPCART, shopcartInfo);

        //清除cookie
        CookieUtils.setCookie(request, response, "shopcart", shopcartInfo, true);

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
