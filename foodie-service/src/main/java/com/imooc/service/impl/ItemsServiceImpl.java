package com.imooc.service.impl;

import com.imooc.mapper.CarouselMapper;
import com.imooc.mapper.ItemsMapperCustom;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.vo.CategoryItemsVO;
import com.imooc.service.CarouselService;
import com.imooc.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ItemsServiceImpl implements ItemsService {

    @Autowired
    public ItemsMapperCustom itemsMapperCustom;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryItemsVO> getSixNewItemByRootCatId(Integer rootCatId) {
        return itemsMapperCustom.getSixNewItemByRootCatId(rootCatId);
    }
}
