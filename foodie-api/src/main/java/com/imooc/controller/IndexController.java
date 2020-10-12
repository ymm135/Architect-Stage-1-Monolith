package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.mapper.CarouselMapper;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.service.ItemsService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Api(value = "首页", tags = "首页相关接口")
@RestController
@RequestMapping("index")
public class IndexController {

    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemsService itemsService;

    @ApiOperation(value = "获取轮播图", httpMethod = "GET", tags = "")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel(){
        List<Carousel> carousels = carouselService.queryAll(YesOrNo.YES.type);
        return IMOOCJSONResult.ok(carousels);
    }


    @ApiOperation(value = "获取父级分类", httpMethod = "GET", tags = "")
    @GetMapping("/cats")
    public IMOOCJSONResult cats(){
        List<Category> categories = categoryService.queryAllRootLevelCats();
        return IMOOCJSONResult.ok(categories);
    }

    @ApiOperation(value = "获取子集分类", httpMethod = "GET", tags = "")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(@PathVariable Integer rootCatId){
        if(rootCatId == null){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }
        return IMOOCJSONResult.ok(categoryService.queryAllSubLevelCats(rootCatId));
    }

    @ApiOperation(value = "通过分类获取最新商品", httpMethod = "GET", tags = "")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(@PathVariable Integer rootCatId){
        if(rootCatId == null){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }
        return IMOOCJSONResult.ok(itemsService.getSixNewItemByRootCatId(rootCatId));
    }

}
