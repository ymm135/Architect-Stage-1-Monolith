package com.imooc.pojo.vo;

import com.imooc.pojo.bo.ShopCartBO;

import java.util.List;

public class OrderVO {
    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopCartBO> toBeRemoveShopCartList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }

    public List<ShopCartBO> getToBeRemoveShopCartList() {
        return toBeRemoveShopCartList;
    }

    public void setToBeRemoveShopCartList(List<ShopCartBO> toBeRemoveShopCartList) {
        this.toBeRemoveShopCartList = toBeRemoveShopCartList;
    }
}
