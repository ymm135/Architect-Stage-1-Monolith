package com.imooc.service.user_center;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.pojo.vo.user_center.OrderStatusCountsVO;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MyOrderService {

    PagedGridResult queryOrders(String userId, Integer orderStatus,
                                Integer page, Integer pageSize);

    IMOOCJSONResult checkUserAndOrder(String userId, String orderId);

    boolean confirmReceive(String userId, String orderId);


    boolean delete(String userId, String orderId);

    OrderStatusCountsVO getUserOrderCount(String userId);

    PagedGridResult getUserOrderTrend(String userId, Integer page, Integer pageSize);
}
