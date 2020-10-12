package com.imooc.service;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBo;

import java.util.List;

public interface AddressService {
    /**
     *
     * @param userId
     * @return
     */
    List<UserAddress> queryAllAddressByUserId(String userId);

    /**
     *
     */
    void addUserAddress(AddressBo addressBo);


    void updateUserAddress(AddressBo addressBo);

    void deleteUserAddress(String userId, String addressId);

    void updateUserAddressToDefault(String userId, String addressId);
}
