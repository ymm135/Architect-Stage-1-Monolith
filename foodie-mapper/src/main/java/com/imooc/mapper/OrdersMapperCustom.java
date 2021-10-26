package com.imooc.mapper;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.vo.user_center.OrdersVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapperCustom {

    List<OrdersVO> queryOrders(@Param("paramsMap")Map paramsMap);

    int getUserOrderCount(@Param("paramsMap")Map paramsMap);

    List<OrderStatus> getUserOrderTrend(@Param("paramsMap")Map paramsMap);
}