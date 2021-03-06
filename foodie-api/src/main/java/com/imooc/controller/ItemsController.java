package com.imooc.controller;

import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.ItemsInfoVO;
import com.imooc.pojo.vo.ShopCartVO;
import com.imooc.service.ItemsService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;

@Api(value = "商品相关接口", tags = "包含商品详情、规格、价格、评论等信息")
@RestController
@Validated
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
                    String itemId) {
        logger.info("getItemsInfo " + itemId);

        if (StringUtils.isEmpty(itemId)) {
            return IMOOCJSONResult.errorMsg("商品ID不能为空");
        }

        ItemsInfoVO itemsInfoVO = new ItemsInfoVO();
        itemsInfoVO.setItem(itemsService.queryItemById(itemId));
        itemsInfoVO.setItemImgList(itemsService.queryItemImgById(itemId));
        itemsInfoVO.setItemSpecList(itemsService.queryItemSpecById(itemId));
        itemsInfoVO.setItemParams(itemsService.queryItemParamById(itemId));

        return IMOOCJSONResult.ok(itemsInfoVO);
    }

    @ApiOperation(value = "商品评论查询", httpMethod = "GET", tags = "商品评论查询")
    @GetMapping("/commentLevel")
    public IMOOCJSONResult getItemLevel(
            @ApiParam(name = "itemId", value = "商品ID", required = true)
            @RequestParam("itemId")
                    String itemId) {
        logger.info("getItemLevel =========================== " + itemId);

        if (StringUtils.isEmpty(itemId)) {
            return IMOOCJSONResult.errorMsg("商品ID不能为空");
        }

        CommentLevelCountVO commentLevelCountVO = itemsService.queryItemCommentById(itemId);

        return IMOOCJSONResult.ok(commentLevelCountVO);
    }

    @ApiOperation(value = "商品评论列表", httpMethod = "GET", tags = "商品评论列表")
    @GetMapping("/comments")
    public IMOOCJSONResult getItemComments(
            @ApiParam(name = "itemId", value = "商品ID", required = true)
            @RequestParam("itemId")
                    String itemId,
            @ApiParam(name = "评论类别", value = "评论分类", required = true)
            @RequestParam("level")
                    Integer level,
            @ApiParam(name = "当前页", value = "当前页" , required = true)
            @RequestParam("page")
                    Integer page,
            @ApiParam(name = "每页显示个数", value = "每页显示个数", required = true)
            @RequestParam("pageSize") @Max(100)
                    Integer pageSize) {
        logger.info("getItemComments =========================== " + itemId);

        if (StringUtils.isEmpty(itemId)) {
            return IMOOCJSONResult.errorMsg("商品ID不能为空");
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult itemsComments = itemsService.getItemsComments(itemId, level, page, pageSize);

        return IMOOCJSONResult.ok(itemsComments);
    }

    @ApiOperation(value = "商品搜索", httpMethod = "GET", tags = "商品搜索")
    @GetMapping("/search")
    public IMOOCJSONResult searchItems(
            @ApiParam(name = "keywords", value = "搜索的关键字", required = true)
            @RequestParam("keywords")
                    String keywords,
            @ApiParam(name = "sort", value = "排序方式", required = true)
            @RequestParam("sort")
                    String sort,
            @ApiParam(name = "page", value = "当前页" , required = true)
            @RequestParam("page")
                    Integer page,
            @ApiParam(name = "pageSize", value = "每页显示个数", required = true)
            @RequestParam("pageSize") @Max(100)
                    Integer pageSize) {
        logger.info("searchItems =========================== " + keywords);

        if (StringUtils.isEmpty(keywords)) {
            return IMOOCJSONResult.errorMsg("搜索的关键字不能为空");
        }

        if(StringUtils.isEmpty(sort)){
            sort = "p";
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult itemsComments = itemsService.searchItems(keywords, sort, page, pageSize);

        return IMOOCJSONResult.ok(itemsComments);
    }


    @ApiOperation(value = "商品搜索", httpMethod = "GET", tags = "商品搜索")
    @GetMapping("/catItems")
    public IMOOCJSONResult searchItemsByCatId(
            @ApiParam(name = "catId", value = "三级分类ID", required = true)
            @RequestParam("catId")
                    Integer catId,
            @ApiParam(name = "sort", value = "排序方式", required = true)
            @RequestParam("sort")
                    String sort,
            @ApiParam(name = "page", value = "当前页" , required = true)
            @RequestParam("page")
                    Integer page,
            @ApiParam(name = "pageSize", value = "每页显示个数", required = true)
            @RequestParam("pageSize") @Max(100)
                    Integer pageSize) {
        logger.info("searchItems =========================== " + catId);

        if (catId == null) {
            return IMOOCJSONResult.errorMsg("搜索的关键字不能为空");
        }

        if(StringUtils.isEmpty(sort)){
            sort = "p";
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult itemsComments = itemsService.searchItemsByThirdCat(catId, sort, page, pageSize);

        return IMOOCJSONResult.ok(itemsComments);
    }

    @ApiOperation(value = "查询商品最新的价格", httpMethod = "GET", tags = "查询商品最新的价格")
    @GetMapping("/refresh")
    public IMOOCJSONResult refresh(
            @ApiParam(name = "itemSpecIds", value = "商品规格拼接的Id", required = true, example = "1,2)")
            @RequestParam
                    String itemSpecIds) {
        logger.info("refresh =========================== " + itemSpecIds);

        if (StringUtils.isEmpty(itemSpecIds)) {
            return IMOOCJSONResult.ok();
        }

        List<ShopCartVO> shopCartVOS = itemsService.queryItemBySpecId(itemSpecIds);

        return IMOOCJSONResult.ok(shopCartVOS);
    }

}
