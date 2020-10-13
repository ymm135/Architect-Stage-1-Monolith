package com.imooc.mapper;

import com.imooc.pojo.vo.CategoryItemsVO;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.pojo.vo.SearchItemsVO;
import com.imooc.pojo.vo.ShopCartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {
    /**
     *
     * @param rootCatId
     * @return
     */
    List<CategoryItemsVO> getSixNewItemByRootCatId(Integer rootCatId);

    /**
     *
     * @param params
     * @return
     */
    List<CommentsVO> queryItemComments(@Param("map") Map params);


    /**
     *
     * @param params
     * @return
     */
    List<SearchItemsVO> searchItems(@Param("map") Map params);

    /**
     *
     * @param params
     * @return
     */
    List<SearchItemsVO> searchItemsByThirdCat(@Param("map") Map params);


    List<ShopCartVO> queryItemBySpecId(@Param("paramsList") List params);


    int decreaseItemSpecStock(@Param("specId") String specId, @Param("pendingCounts")Integer pendingCounts);

}