package com.imooc.service.impl.user_center;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.mapper.OrdersMapperCustom;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.user_center.OrderStatusCountsVO;
import com.imooc.pojo.vo.user_center.OrdersVO;
import com.imooc.service.user_center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyOrderServiceImpl implements MyOrderService {

    @Autowired
    private OrdersMapperCustom ordersMapperCustom;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Override
    public PagedGridResult queryOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId",userId);
        paramsMap.put("orderStatus",orderStatus);

        PageHelper.startPage(page, pageSize);

        List<OrdersVO> ordersVOS = ordersMapperCustom.queryOrders(paramsMap);

        PagedGridResult pagedGridResult = getPagedGridResult(page, ordersVOS);

        return pagedGridResult;
    }


    private PagedGridResult getPagedGridResult(Integer page, List<?> list) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult pagedGridResult = new PagedGridResult();
        //当前页
        pagedGridResult.setPage(page);
        //每行显示内容
        pagedGridResult.setRows(list);
        //总页数
        pagedGridResult.setTotal(pageList.getPages());
        //总记录数
        pagedGridResult.setRecords(pageList.getTotal());
        return pagedGridResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean confirmReceive(String userId, String orderId) {

        OrderStatus confirmOS = new OrderStatus();
        confirmOS.setOrderStatus(OrderStatusEnum.SUCCESS.type);
        confirmOS.setSuccessTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);

        int res = orderStatusMapper.updateByExampleSelective(confirmOS, example);

        return res == 1 ? true : false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean delete(String userId, String orderId) {
        Orders orders = new Orders();
        orders.setIsDelete(YesOrNo.YES.type);
        orders.setUpdatedTime(new Date());

        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orderId);
        criteria.andEqualTo("userId", userId);

        int deleteRes = ordersMapper.updateByExampleSelective(orders, example);
        return deleteRes == 1 ? true : false;
    }

    @Override
    public IMOOCJSONResult checkUserAndOrder(String userId, String orderId) {
        IMOOCJSONResult result = null;

        //首选确认用户与该订单是否关联，防止恶意修改
        Orders orders = new Orders();
        orders.setUserId(userId);
        orders.setId(orderId);

        Orders ordersRes = ordersMapper.selectOne(orders);
        if(ordersRes != null){
            result = IMOOCJSONResult.ok(ordersRes);
        }else {
            result = IMOOCJSONResult.errorMsg("该用户没有该订单!!");
        }
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatusCountsVO getUserOrderCount(String userId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId",userId);
        paramsMap.put("orderStatus",OrderStatusEnum.WAIT_PAY.type);

        //待付款
        int waitPayCount = ordersMapperCustom.getUserOrderCount(paramsMap);


        //待发货
        paramsMap.put("orderStatus",OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliveryCount = ordersMapperCustom.getUserOrderCount(paramsMap);

        //待收货
        paramsMap.put("orderStatus",OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCount = ordersMapperCustom.getUserOrderCount(paramsMap);

        //待评论
        paramsMap.put("orderStatus",OrderStatusEnum.SUCCESS.type);
        paramsMap.put("isComment", YesOrNo.NO.type);
        int pendingCommentCount = ordersMapperCustom.getUserOrderCount(paramsMap);

        OrderStatusCountsVO orderStatusCountsVO = new OrderStatusCountsVO();
        orderStatusCountsVO.setWaitPayCounts(waitPayCount);
        orderStatusCountsVO.setWaitDeliverCounts(waitDeliveryCount);
        orderStatusCountsVO.setWaitReceiveCounts(waitReceiveCount);
        orderStatusCountsVO.setWaitCommentCounts(pendingCommentCount);

        return orderStatusCountsVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getUserOrderTrend(String userId, Integer page, Integer pageSize) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId",userId);

        PageHelper.startPage(page, pageSize);

        List<OrderStatus> userOrderTrend = ordersMapperCustom.getUserOrderTrend(paramsMap);

        PagedGridResult pagedGridResult = getPagedGridResult(page, userOrderTrend);

        return pagedGridResult;
    }
}
