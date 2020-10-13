package com.imooc.service;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.OrderVO;

public interface OrderService {

    /**
     * 创建订单
     * @param submitOrderBO
     */
    OrderVO createOrder(SubmitOrderBO submitOrderBO);

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    OrderStatus getPaidOrderInfo(String orderId);


    void updateOrderStatus(String orderId, Integer orderStatus);


    void closeOrders();
}
