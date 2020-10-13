package com.imooc.service.impl;

import com.imooc.enums.YesOrNo;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBo;
import com.imooc.service.AddressService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAllAddressByUserId(String userId) {

//        Example example = new Example(UserAddress.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("userId", userId);
//
//        List<UserAddress> userAddresses = userAddressMapper.selectByExample(example);

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);

        List<UserAddress> userAddresses = userAddressMapper.select(userAddress);

        return userAddresses;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void addUserAddress(AddressBo addressBo) {
        //首选判断用户是否存在收获地址
        List<UserAddress> userAddresses = queryAllAddressByUserId(addressBo.getUserId());
        Integer isFirstAddress = 0;

        if(!(userAddresses != null && userAddresses.size() > 0)){//存在收获地址
            isFirstAddress = 1;
        }

        String userId = sid.nextShort();

        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBo, userAddress);


        userAddress.setId(userId);
        userAddress.setIsDefault(isFirstAddress);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(userAddress);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void updateUserAddress(AddressBo addressBo) {
        String addressId = addressBo.getAddressId();
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBo, userAddress);

        userAddress.setId(addressId);
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setId(addressId);

        userAddressMapper.delete(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddressToDefault(String userId, String addressId) {
        //查询默认地址，修改为不默认
        UserAddress userAddress = new UserAddress();
        userAddress.setIsDefault(YesOrNo.YES.type);
        userAddress.setUserId(userId);

        List<UserAddress> UserAddressDefaults = userAddressMapper.select(userAddress);
        for (UserAddress address : UserAddressDefaults){
            address.setIsDefault(YesOrNo.NO.type);

            userAddressMapper.updateByPrimaryKeySelective(address);
        }

        //默认新地址
        UserAddress userAddressNewDef = new UserAddress();
        userAddressNewDef.setId(addressId);
        userAddressNewDef.setUserId(userId);
        userAddressNewDef.setIsDefault(YesOrNo.YES.type);

        userAddressMapper.updateByPrimaryKeySelective(userAddressNewDef);

    }

    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        userAddress.setUserId(userId);

        UserAddress userAddressRes = userAddressMapper.selectOne(userAddress);

        return userAddressRes;
    }
}
