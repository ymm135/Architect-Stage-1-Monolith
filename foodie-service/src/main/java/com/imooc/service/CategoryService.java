package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<Category> queryAllRootLevelCats();
    List<CategoryVO> queryAllSubLevelCats(Integer fatherId);
}
