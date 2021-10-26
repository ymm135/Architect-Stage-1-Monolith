package com.imooc.service.impl;

import com.imooc.pojo.Items;
import com.imooc.service.ItemsService;
import com.imooc.utils.PagedGridResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemsServiceImpl implements ItemsService {

    private Logger logger = LoggerFactory.getLogger(ItemsServiceImpl.class.getSimpleName());

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {

        //es 分页从0开始
        --page;

        String preTag = "<font color='red' >";
        String postTag = "</font>";

        Pageable pageable = PageRequest.of(page, pageSize);

        String sortField;
        SortOrder sortOrder;

        if ("c".equals(sort)) {//销量排序
            sortField = "sellCounts";
            sortOrder = SortOrder.DESC;
        } else if ("p".equals(sort)) {//价格排序
            sortField = "price";
            sortOrder = SortOrder.ASC;
        } else {//默认是名称排序
            sortField = "itemName.keyword";
            sortOrder = SortOrder.ASC;
        }

        SortBuilder sortBuilder = new FieldSortBuilder(sortField).order(sortOrder);

        String key = "itemName";
        SearchQuery searchQuery =
                new NativeSearchQueryBuilder()
                        .withQuery(QueryBuilders.matchQuery(key, keywords))
                        .withHighlightFields(new HighlightBuilder.Field(key))
                        .withSort(sortBuilder)
                        .withPageable(pageable)
                        .build();

        AggregatedPage<Items> itemsPages = esTemplate.queryForPage(searchQuery, Items.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<Items> hlItems = new ArrayList<>();
                SearchHits hits = response.getHits();

                for (SearchHit hit : hits) {
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    HighlightField itemsHLF = highlightFields.get(key);
                    String itemNameRes = itemsHLF.getFragments()[0].toString();

                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    Object itemId = sourceAsMap.get("itemId");
                    Object imgUrl = sourceAsMap.get("imgUrl");
                    Object price = sourceAsMap.get("price");
                    Object sellCounts = sourceAsMap.get("sellCounts");

                    Items itemsResult = new Items();
                    itemsResult.setItemId(String.valueOf(itemId));
                    itemsResult.setItemName(String.valueOf(itemNameRes));
                    itemsResult.setImgUrl(String.valueOf(imgUrl));
                    itemsResult.setPrice(Integer.valueOf(price.toString()));
                    itemsResult.setSellCounts(Integer.valueOf(sellCounts.toString()));
                    hlItems.add(itemsResult);
                }

                return new AggregatedPageImpl<>((List<T>) hlItems, pageable, hits.getTotalHits());
            }

        });

        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRecords(itemsPages.getTotalElements());
        pagedGridResult.setTotal(itemsPages.getTotalPages());
        pagedGridResult.setRows(itemsPages.getContent());
        pagedGridResult.setPage(++page);

        return pagedGridResult;
    }

}
