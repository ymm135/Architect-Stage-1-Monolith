package com.imooc.service;

import com.imooc.pojo.vo.CategoryItemsVO;

import java.util.List;

public interface ItemsService {
    List<CategoryItemsVO> getSixNewItemByRootCatId(Integer rootCatId);
}
