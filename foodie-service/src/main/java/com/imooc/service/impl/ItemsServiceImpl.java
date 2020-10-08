package com.imooc.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.CommentLevel;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CategoryItemsVO;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.service.ItemsService;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemsServiceImpl implements ItemsService {

    @Autowired
    public ItemsMapperCustom itemsMapperCustom;

    @Autowired
    public ItemsMapper itemsMapper;

    @Autowired
    public ItemsImgMapper itemsImgMapper;

    @Autowired
    public ItemsSpecMapper itemsSpecMapper;

    @Autowired
    public ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryItemsVO> getSixNewItemByRootCatId(Integer rootCatId) {
        return itemsMapperCustom.getSixNewItemByRootCatId(rootCatId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {
        Items item = itemsMapper.selectByPrimaryKey(itemId);
        return item;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgById(String itemId) {
        Example example = new Example(ItemsImg.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        List<ItemsImg> itemsImgs = itemsImgMapper.selectByExample(example);
        return itemsImgs;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecById(String itemId) {
        Example example = new Example(ItemsSpec.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        List<ItemsSpec> itemsImgs = itemsSpecMapper.selectByExample(example);
        return itemsImgs;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParamById(String itemId) {
        Example example = new Example(ItemsParam.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        ItemsParam itemsImgs = itemsParamMapper.selectOneByExample(example);
        return itemsImgs;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountVO queryItemCommentById(String itemId) {
        CommentLevelCountVO commentLevelCountVO = new CommentLevelCountVO();
        int goods = getCommentCount(itemId, CommentLevel.GOOD.type);
        int normals = getCommentCount(itemId, CommentLevel.NORMAL.type);
        int bads = getCommentCount(itemId, CommentLevel.BAD.type);

        commentLevelCountVO.setGoodCounts(goods);
        commentLevelCountVO.setNormalCounts(normals);
        commentLevelCountVO.setBadCounts(bads);

        commentLevelCountVO.setTotalCounts(goods + normals + bads);

        return commentLevelCountVO;
    }


    private int getCommentCount(String itemId, Integer level){
        int res = 0;

        ItemsComments itemsComments = new ItemsComments();
        itemsComments.setItemId(itemId);

        if(level != null){
            itemsComments.setCommentLevel(level);
        }

        res = itemsCommentsMapper.selectCount(itemsComments);

        return res;
    }


    /**
     *
     * @param itemId
     * @param commentLevel
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getItemsComments(String itemId, Integer commentLevel,
                                             Integer page, Integer pageSize){
        Map<String,Object> params = new HashMap<>();
        params.put("itemId", itemId);
        params.put("level", commentLevel);

        //分页查询  通过统一拦截sql
        Page<Object> objects = PageHelper.startPage(page, pageSize);
        List<CommentsVO> comments = itemsMapperCustom.queryItemComments(params);

        //脱敏操作
        for (CommentsVO comment : comments){
            comment.setNickname(DesensitizationUtil.commonDisplay(comment.getNickname()));
        }

        PageInfo<?> pageList = new PageInfo<>(comments);
        PagedGridResult pagedGridResult = new PagedGridResult();
        //当前页
        pagedGridResult.setPage(page);
        //每行显示内容
        pagedGridResult.setRows(comments);
        //总页数
        pagedGridResult.setTotal(pageList.getPages());
        //总记录数
        pagedGridResult.setRecords(pageList.getTotal());

        return pagedGridResult;
    }
}
