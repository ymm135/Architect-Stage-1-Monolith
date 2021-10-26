package com.imooc.pojo.vo.user_center;

import java.util.List;

public class OrdersVO {
    private String orderId;
    private String createdTime;
    private Integer payMethod;
    private Integer realPayAmount;
    private String postAmount;
    private Integer orderStatus;
    private Integer isComment;

    List<OrderItemsVO> subOrderItemList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    public Integer getRealPayAmount() {
        return realPayAmount;
    }

    public void setRealPayAmount(Integer realPayAmount) {
        this.realPayAmount = realPayAmount;
    }

    public String getPostAmount() {
        return postAmount;
    }

    public void setPostAmount(String postAmount) {
        this.postAmount = postAmount;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getIsComment() {
        return isComment;
    }

    public void setIsComment(Integer isComment) {
        this.isComment = isComment;
    }

    public List<OrderItemsVO> getSubOrderItemList() {
        return subOrderItemList;
    }

    public void setSubOrderItemList(List<OrderItemsVO> subOrderItemList) {
        this.subOrderItemList = subOrderItemList;
    }
}
