package com.imooc.controller;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBo;
import com.imooc.service.AddressService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址相关接口
 * 1、查询用户所有收获地址
 * 2、增加、修改、删除、默认收货地址
 */
@Api(value = "地址相关接口", tags = "地址相关接口")
@RequestMapping("address")
@RestController
public class AddressController {

    private final static Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AddressService addressService;

    @ApiOperation(value = "查询用户收获地址", httpMethod = "GET", tags = "查询用户收获地址")
    @PostMapping("/list")
    public IMOOCJSONResult getAddress(@RequestParam String userId){

        if(StringUtils.isEmpty(userId)){
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        List<UserAddress> userAddresses = addressService.queryAllAddressByUserId(userId);

        return IMOOCJSONResult.ok(userAddresses);
    }


    @ApiOperation(value = "增加收货地址", httpMethod = "POST", tags = "增加收货地址")
    @PostMapping("/add")
    public IMOOCJSONResult add(@RequestBody AddressBo addressBo){

        IMOOCJSONResult imoocjsonResult = checkAddressBo(addressBo);
        if(!imoocjsonResult.isOK()){
            return imoocjsonResult;
        }

        addressService.addUserAddress(addressBo);
        return IMOOCJSONResult.ok();
    }

    private IMOOCJSONResult checkAddressBo(AddressBo addressBo){
        if(StringUtils.isEmpty(addressBo.getUserId())){
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        if(StringUtils.isEmpty(addressBo.getReceiver())){
            return IMOOCJSONResult.errorMsg("收件人不能为空");
        }

        String mobile = addressBo.getMobile();

        if(StringUtils.isEmpty(mobile)){
            return IMOOCJSONResult.errorMsg("手机号不能为空");
        }

        if(mobile.length() != 11){
            return IMOOCJSONResult.errorMsg("手机号不能为空");
        }

        if(StringUtils.isEmpty(addressBo.getUserId())){
            return IMOOCJSONResult.errorMsg("手机号的长度不正确");
        }

        boolean isCorectMobile = MobileEmailUtils.checkMobileIsOk(mobile);

        if(isCorectMobile){
            return IMOOCJSONResult.errorMsg("手机号格式不正确");
        }

        String city = addressBo.getCity();
        String district = addressBo.getDistrict();
        String province = addressBo.getProvince();
        String detail = addressBo.getDetail();

        if(StringUtils.isEmpty(city) || StringUtils.isEmpty(district) ||
            StringUtils.isEmpty(province) || StringUtils.isEmpty(detail)){
            return IMOOCJSONResult.errorMsg("收获地址信息不完整");
        }

        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "更新收货地址", httpMethod = "POST", tags = "更新收货地址")
    @PostMapping("/update")
    public IMOOCJSONResult update(@RequestBody AddressBo addressBo){

        if(StringUtils.isEmpty(addressBo.getAddressId())){
            return IMOOCJSONResult.errorMsg("addressId不能为空");
        }

        IMOOCJSONResult imoocjsonResult = checkAddressBo(addressBo);
        if(!imoocjsonResult.isOK()){
            return imoocjsonResult;
        }

        addressService.updateUserAddress(addressBo);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "删除收货地址", httpMethod = "POST", tags = "删除收货地址")
    @PostMapping("/delete")
    public IMOOCJSONResult delete(@RequestParam String userId, @RequestParam String addressId){

        if(StringUtils.isEmpty(userId)){
            return IMOOCJSONResult.errorMsg("userId不能为空");
        }

        if(StringUtils.isEmpty(addressId)){
            return IMOOCJSONResult.errorMsg("addressId不能为空");
        }

        addressService.deleteUserAddress(userId, addressId);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "删除收货地址", httpMethod = "POST", tags = "删除收货地址")
    @PostMapping("/setDefalut")
    public IMOOCJSONResult setDefalut(@RequestParam String userId, @RequestParam String addressId){

        if(StringUtils.isEmpty(userId)){
            return IMOOCJSONResult.errorMsg("userId不能为空");
        }

        if(StringUtils.isEmpty(addressId)){
            return IMOOCJSONResult.errorMsg("addressId不能为空");
        }

        addressService.updateUserAddressToDefault(userId, addressId);

        return IMOOCJSONResult.ok();
    }

}