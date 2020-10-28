package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.mapper.CarouselMapper;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.service.ItemsService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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


    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取轮播图", httpMethod = "GET", tags = "")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel(){

        //存储到redis中
        List<Carousel> carousels = null;
        final String REDIS_KEY_CAROUSEL = "carousel";
        String carousel = redisOperator.get(REDIS_KEY_CAROUSEL);

        if(StringUtils.isBlank(carousel)){
            carousels = carouselService.queryAll(YesOrNo.YES.type);
            redisOperator.set(REDIS_KEY_CAROUSEL, JsonUtils.objectToJson(carousels));
        }else{
            carousels = JsonUtils.jsonToList(carousel, Carousel.class);
        }

        return IMOOCJSONResult.ok(carousels);
    }


    @ApiOperation(value = "获取父级分类", httpMethod = "GET", tags = "")
    @GetMapping("/cats")
    public IMOOCJSONResult cats(){

        //存储到redis中
        List<Category> categories = null;
        final String REDIS_KEY_CAROUSEL = "category";
        String category = redisOperator.get(REDIS_KEY_CAROUSEL);

        if(StringUtils.isBlank(category)){
            categories = categoryService.queryAllRootLevelCats();
            redisOperator.set(REDIS_KEY_CAROUSEL, JsonUtils.objectToJson(categories));
        }else{
            categories = JsonUtils.jsonToList(category, Category.class);
        }

        return IMOOCJSONResult.ok(categories);
    }

    @ApiOperation(value = "获取子集分类", httpMethod = "GET", tags = "")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(@PathVariable Integer rootCatId){
        if(rootCatId == null){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        //存储到redis中
        List<CategoryVO> categoryVOS = null;
        final String REDIS_KEY_SUBCAT = "subCat" + ":" + rootCatId;
        String subcats = redisOperator.get(REDIS_KEY_SUBCAT);

        if(StringUtils.isBlank(subcats)){
            categoryVOS = categoryService.queryAllSubLevelCats(rootCatId);

            if(categoryVOS != null && categoryVOS.size() > 0) {
                redisOperator.set(REDIS_KEY_SUBCAT, JsonUtils.objectToJson(categoryVOS));
            }else {
                redisOperator.set(REDIS_KEY_SUBCAT, JsonUtils.objectToJson(categoryVOS), 5 * 60);
            }
        }else{
            categoryVOS = JsonUtils.jsonToList(subcats, CategoryVO.class);
        }

        return IMOOCJSONResult.ok(categoryVOS);
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
