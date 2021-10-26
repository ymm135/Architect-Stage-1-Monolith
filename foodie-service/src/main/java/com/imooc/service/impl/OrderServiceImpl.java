package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.ItemsMapperCustom;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.ShopCartBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.pojo.vo.ShopCartVO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemsService;
import com.imooc.service.OrderService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.metadata.CommonsDbcp2DataSourcePoolMetadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;


    @Autowired
    private RedisOperator redisOperator;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public IMOOCJSONResult createOrder(SubmitOrderBO submitOrderBO) {
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        String leftMsg = submitOrderBO.getLeftMsg();
        Integer payMethod = submitOrderBO.getPayMethod();
        String userId = submitOrderBO.getUserId();

        //包邮费用为零
        Integer postAmount = 0;

        //1、新订单数据保存
        Orders orders = new Orders();

        String orderId = sid.nextShort();
        orders.setId(orderId);
        orders.setUserId(userId);

        UserAddress userAddress = addressService.queryUserAddress(userId, addressId);
        orders.setReceiverName(userAddress.getReceiver());
        orders.setReceiverAddress(userAddress.getProvince() + " " + userAddress.getCity() + " " + userAddress.getDistrict() + " " + userAddress.getDetail());
        orders.setReceiverMobile(userAddress.getMobile());

        orders.setPostAmount(postAmount);

        orders.setPayMethod(payMethod);
        orders.setLeftMsg(leftMsg);
        orders.setIsComment(YesOrNo.NO.type);
        orders.setIsDelete(YesOrNo.NO.type);

        Date date = new Date();
        orders.setCreatedTime(date);
        orders.setUpdatedTime(date);

        //2、根据specIds计算价格
        String[] ids = itemSpecIds.split(",");
        Integer totalAmount = 0;//总价格
        Integer realPayAmount = 0;//实际支付价格

        Integer buyConuts = 1;

        String RESIS_KEY_SHOPCART = "shopcart:" + userId;
        String shopcartItemsStr = redisOperator.get(RESIS_KEY_SHOPCART);
        List<ShopCartBO> shopCartBOList = null;
        List<ShopCartBO> toBeRemoveShopCartList = new ArrayList<>();

        if (!StringUtils.isBlank(shopcartItemsStr)) {
            shopCartBOList = JsonUtils.jsonToList(shopcartItemsStr, ShopCartBO.class);
        } else {
            return IMOOCJSONResult.errorMsg("购物车信息为空!!");
        }

        for (String id : ids) {

            ShopCartBO shopCartItem = getShopCartFromList(shopCartBOList, id);
            if (shopCartItem == null) {
                return IMOOCJSONResult.errorMsg("购物车找不到该商品!");
            }

            toBeRemoveShopCartList.add(shopCartItem);

            buyConuts = shopCartItem.getBuyCounts();

            //获取价格
            ItemsSpec itemsSpec = itemsService.queryItemSpecBySpecId(id);
            totalAmount += itemsSpec.getPriceNormal() * buyConuts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyConuts;

            //获取商品信息
            Items items = itemsService.queryItemById(itemsSpec.getItemId());
            String imgUrl = itemsService.queryItemMainImgById(itemsSpec.getItemId());

            //循环保存子订单的信息
            String subOrderItemId = sid.nextShort();
            OrderItems subOrderItems = new OrderItems();

            subOrderItems.setId(subOrderItemId);
            subOrderItems.setBuyCounts(buyConuts);
            subOrderItems.setItemId(itemsSpec.getItemId());
            subOrderItems.setItemName(items.getItemName());
            subOrderItems.setItemImg(imgUrl);
            subOrderItems.setItemSpecId(itemsSpec.getId());
            subOrderItems.setOrderId(orderId);
            subOrderItems.setPrice(itemsSpec.getPriceDiscount());
            subOrderItems.setItemSpecName(itemsSpec.getName());

            orderItemsMapper.insert(subOrderItems);

            //用户提交订单以后，需要扣除库存
            itemsService.decreaseItemSpecStock(itemsSpec.getId(), buyConuts);

        }

        orders.setTotalAmount(totalAmount);
        orders.setRealPayAmount(realPayAmount);
        ordersMapper.insert(orders);

        //3、创建订单状态
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);

        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        //4、构建商户订单，用于传给用于中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setPayMethod(payMethod);
        merchantOrdersVO.setAmount(realPayAmount + postAmount);

        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setToBeRemoveShopCartList(toBeRemoveShopCartList);

        return IMOOCJSONResult.ok(orderVO);
    }

    private ShopCartBO getShopCartFromList(List<ShopCartBO> list, String specId) {
        for (ShopCartBO shopCartItem : list) {
            if (shopCartItem.getSpecId().equals(specId)) {
                return shopCartItem;
            }
        }

        return null;
    }

    @Override
    public OrderStatus getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);

        return orderStatus;
    }

    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidOrderStatus = new OrderStatus();
        paidOrderStatus.setOrderId(orderId);
        paidOrderStatus.setPayTime(new Date());
        paidOrderStatus.setOrderStatus(orderStatus);

        orderStatusMapper.updateByPrimaryKeySelective(paidOrderStatus);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrders() {
        //查询所有未付款订单，超时时间为1d
        OrderStatus queryOrder = new OrderStatus();
        queryOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> waitPayOrders = orderStatusMapper.select(queryOrder);

        for (OrderStatus waitPayOrder : waitPayOrders) {
            //检查超时时间
            Date createdTime = waitPayOrder.getCreatedTime();
            int days = DateUtil.daysBetween(createdTime, new Date());

            if (days >= 1) {
                doCloseOrder(waitPayOrder.getOrderId());
            }

        }
    }

    private void doCloseOrder(String orderId) {
        OrderStatus closeOrder = new OrderStatus();
        closeOrder.setOrderStatus(OrderStatusEnum.CLOSE.type);
        closeOrder.setOrderId(orderId);
        closeOrder.setCloseTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(closeOrder);
    }

}
