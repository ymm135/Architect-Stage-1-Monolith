package com.imooc.mapper;

import com.imooc.pojo.vo.CategoryItemsVO;

import java.util.List;

public interface ItemsMapperCustom {
    public List<CategoryItemsVO> getSixNewItemByRootCatId(Integer rootCatId);
}