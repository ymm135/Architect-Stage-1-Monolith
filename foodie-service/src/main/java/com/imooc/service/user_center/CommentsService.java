package com.imooc.service.user_center;

import com.imooc.pojo.OrderItems;
import com.imooc.pojo.bo.user_center.OrderItemsCommentsBO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface CommentsService {

    List<OrderItems> queryPendingComments(String orderId);

    void saveComments(String userId, String orderId, List<OrderItemsCommentsBO> commentsBOs);

    PagedGridResult queryUserComments(String userId, Integer page, Integer pageSize);
}
