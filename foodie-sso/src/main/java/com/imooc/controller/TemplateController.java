package com.imooc.controller;

import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "模板接口", tags = "模板接口")
@RestController
public class TemplateController {

    private final static Logger logger = LoggerFactory.getLogger(TemplateController.class);

    @ApiOperation(value = "商品信息查询接口", httpMethod = "GET", tags = "查询商品")
    @GetMapping("/hello")
    public IMOOCJSONResult hello(){
        
        return IMOOCJSONResult.ok();
    }

}
