package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CategoryItemsVO;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface ItemsService {
    List<CategoryItemsVO> getSixNewItemByRootCatId(Integer rootCatId);

    /**
     * 根据商品ID查询商品信息
     * @param itemId
     * @return
     */
    Items queryItemById(String itemId);

    /**
     * 查询商品图片信息
     * @param itemId
     * @return
     */
    List<ItemsImg> queryItemImgById(String itemId);

    /**
     * 查询商品规格
     * @param itemId
     * @return
     */
    List<ItemsSpec> queryItemSpecById(String itemId);

    /**
     * 查询商品参数
     * @param itemId
     * @return
     */
    ItemsParam queryItemParamById(String itemId);

    /**
     *
     * @param itemId
     * @return
     */
    CommentLevelCountVO queryItemCommentById(String itemId);

    /**
     *
     * @param itemId
     * @param commentLevel
     * @return
     */
    PagedGridResult getItemsComments(String itemId, Integer commentLevel,
                                     Integer page, Integer pageSize);

}
