package com.imooc.service;

import com.imooc.utils.PagedGridResult;

public interface ItemsService {

    PagedGridResult searchItems(
            String keywords,
            String sort,
            Integer page,
            Integer pageSize);
}
