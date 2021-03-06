package com.imooc.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.CommentLevel;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.*;
import com.imooc.service.ItemsService;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.PagedGridResult;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedissonClient redissonClient;


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

        //????????????  ??????????????????sql
        Page<Object> objects = PageHelper.startPage(page, pageSize);
        List<CommentsVO> comments = itemsMapperCustom.queryItemComments(params);

        //????????????
        for (CommentsVO comment : comments){
            comment.setNickname(DesensitizationUtil.commonDisplay(comment.getNickname()));
        }

        PagedGridResult pagedGridResult = getPagedGridResult(page, comments);

        return pagedGridResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {

        Map<String,Object> params = new HashMap<>();
        params.put("keywords", keywords);
        params.put("sort", sort);

        //????????????  ??????????????????sql
        Page<Object> objects = PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> comments = itemsMapperCustom.searchItems(params);

        PagedGridResult pagedGridResult = getPagedGridResult(page, comments);

        return pagedGridResult;
    }

    private PagedGridResult getPagedGridResult(Integer page, List<?> comments) {
        PageInfo<?> pageList = new PageInfo<>(comments);
        PagedGridResult pagedGridResult = new PagedGridResult();
        //?????????
        pagedGridResult.setPage(page);
        //??????????????????
        pagedGridResult.setRows(comments);
        //?????????
        pagedGridResult.setTotal(pageList.getPages());
        //????????????
        pagedGridResult.setRecords(pageList.getTotal());
        return pagedGridResult;
    }

    @Override
    public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String,Object> params = new HashMap<>();
        params.put("catId", catId);
        params.put("sort", sort);

        //????????????  ??????????????????sql
        Page<Object> objects = PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> comments = itemsMapperCustom.searchItemsByThirdCat(params);

        PagedGridResult pagedGridResult = getPagedGridResult(page, comments);

        return pagedGridResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopCartVO> queryItemBySpecId(String specId) {
        String[] ids = specId.split(",");
        List<String> list = new ArrayList<>();

        Collections.addAll(list, ids);
        return itemsMapperCustom.queryItemBySpecId(list);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemSpecBySpecId(String specId) {
        ItemsSpec itemsSpec = itemsSpecMapper.selectByPrimaryKey(specId);
        return itemsSpec;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.type);

        ItemsImg res = itemsImgMapper.selectOne(itemsImg);

        return res == null ? null : res.getUrl();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public  void decreaseItemSpecStock(String specId, Integer bugCounts) {
        //????????????????????????synchronized
        //????????????????????????
        //????????????
        RLock lock = redissonClient.getLock("Order-Item-" + specId);
        lock.lock(5, TimeUnit.SECONDS);

        try {
            //?????????
            int res = itemsMapperCustom.decreaseItemSpecStock(specId, bugCounts);
            if(res != 1){
                throw new RuntimeException("??????????????????, ??????????????????????????????!");
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }
}
