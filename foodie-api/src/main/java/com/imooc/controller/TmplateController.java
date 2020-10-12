package com.imooc.controller;

import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "商品相关接口", tags = "包含商品详情、规格、价格、评论等信息")
@RestController
public class TmplateController {

    private final static Logger logger = LoggerFactory.getLogger(TmplateController.class);

    @ApiOperation(value = "商品信息查询接口", httpMethod = "GET", tags = "查询商品")
    @GetMapping("/HelloController")
    public IMOOCJSONResult hello(){
        
        return IMOOCJSONResult.ok();
    }

}
