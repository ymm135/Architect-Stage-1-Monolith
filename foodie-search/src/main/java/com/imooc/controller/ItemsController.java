package com.imooc.controller;

import com.imooc.service.ItemsService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "搜索相关接口", tags = "搜索相关接口")
@RestController
public class ItemsController {

    private final static Logger logger = LoggerFactory.getLogger(ItemsController.class);

    @Autowired
    private ItemsService itemsService;

    @GetMapping("/hello")
    public Object hello(){
        
        return "<h1>Hello ES Search!</h1>";
    }

    @GetMapping("/search")
    public IMOOCJSONResult searchItems(
                    String keywords,
                    String sort,
                    Integer page,
                    Integer pageSize) {

        logger.info("searchItems ====" + keywords + ",sort=" + sort + ",page=" + page + ",pageSize=" + pageSize);

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

        logger.info(JsonUtils.objectToJson(itemsComments));

        return IMOOCJSONResult.ok(itemsComments);
    }

}
