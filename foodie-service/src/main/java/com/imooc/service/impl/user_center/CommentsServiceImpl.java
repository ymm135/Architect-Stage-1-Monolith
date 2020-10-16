package com.imooc.service.impl.user_center;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.ItemsCommentsMapperCustom;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.user_center.OrderItemsCommentsBO;
import com.imooc.pojo.vo.user_center.OrdersVO;
import com.imooc.pojo.vo.user_center.UserCommentsVO;
import com.imooc.service.user_center.CommentsService;
import com.imooc.utils.PagedGridResult;
import org.n3r.idworker.Sid;
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
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComments(String orderId) {

        OrderItems orderItems = new OrderItems();
        orderItems.setOrderId(orderId);

        List<OrderItems> orderItemsRes = orderItemsMapper.select(orderItems);

        return orderItemsRes;
    }

    @Override
    public void saveComments(String userId, String orderId, List<OrderItemsCommentsBO> commentsBOs) {
        //保存用户评论items_commemts

        for (OrderItemsCommentsBO commentBO : commentsBOs){
            commentBO.setCommentId(sid.nextShort());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("commentsBOs", commentsBOs);

        itemsCommentsMapperCustom.saveComments(map);

        //更新订单表已评价orders
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setIsComment(YesOrNo.YES.type);

        ordersMapper.updateByPrimaryKeySelective(orders);

        //修改订单状态表，评论时间 orders_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);

        orderStatusMapper.updateByExampleSelective(orderStatus, example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryUserComments(String userId, Integer page, Integer pageSize) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId",userId);

        PageHelper.startPage(page, pageSize);

        List<UserCommentsVO> userCommentsVOS = itemsCommentsMapperCustom.queryUserComments(paramsMap);

        PagedGridResult pagedGridResult = getPagedGridResult(page, userCommentsVOS);

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
}
