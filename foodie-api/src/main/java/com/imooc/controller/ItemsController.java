package com.imooc.controller;

import com.imooc.pojo.vo.ItemsInfoVO;
import com.imooc.service.ItemsService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "商品相关接口", tags = "包含商品详情、规格、价格、评论等信息")
@RestController
@RequestMapping("items")
public class ItemsController {

    private final static Logger logger = LoggerFactory.getLogger(ItemsController.class);

    @Autowired
    private ItemsService itemsService;

    @ApiOperation(value = "商品信息查询接口", httpMethod = "GET", tags = "可以查询商品")
    @GetMapping("/info/{itemId}")
    public IMOOCJSONResult getItemsInfo(
            @ApiParam(name = "itemId", value = "商品ID", required = true)
            @PathVariable("itemId")
                    String itemId){
        logger.info("getItemsInfo " + itemId);

        if(StringUtils.isEmpty(itemId)){
            return IMOOCJSONResult.errorMsg("商品ID不能为空");
        }

        ItemsInfoVO itemsInfoVO = new ItemsInfoVO();
        itemsInfoVO.setItem(itemsService.queryItemById(itemId));
        itemsInfoVO.setItemImgList(itemsService.queryItemImgById(itemId));
        itemsInfoVO.setItemSpecList(itemsService.queryItemSpecById(itemId));
        itemsInfoVO.setItemParams(itemsService.queryItemParamById(itemId));

        return IMOOCJSONResult.ok(itemsInfoVO);
    }

}
