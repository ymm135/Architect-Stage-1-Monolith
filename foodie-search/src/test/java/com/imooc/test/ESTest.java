package com.imooc.test;

import com.imooc.Application;
import com.imooc.pojo.Items;
import com.imooc.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Test
    public void createIndex() {
        for (int i = 0; i < 1000; i++) {
            Stu stu = new Stu();
            stu.setStuId(1000 + i);
            stu.setName("xiao" + i);
            stu.setAge(i % 100);
            stu.setMoney(1f * i);
            stu.setDesc("我是描述:" + UUID.randomUUID().toString().trim());

            IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
            esTemplate.index(indexQuery);
        }
    }

    @Test
    public void deleteIndex() {
        esTemplate.deleteIndex(Stu.class);
    }

    @Test
    public void updateIndexDoc() {

        Map<String, Object> map = new HashMap<>();
        map.put("name", "xiaoming");

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(map);

        UpdateQuery updateQuery =
                new UpdateQueryBuilder()
                        .withClass(Stu.class)
                        .withId("1001")
                        .withIndexRequest(indexRequest)
                        .build();

        esTemplate.update(updateQuery);
    }

    @Test
    public void getIndexDoc() {
        GetQuery getQuery = new GetQuery();
        getQuery.setId("1001");

        Stu stu = esTemplate.queryForObject(getQuery, Stu.class);
        System.out.println(stu);
    }

    @Test
    public void deleteIndexDoc() {
        esTemplate.delete(Stu.class, "1002");
    }


    @Test
    public void testSearch() {
        SearchQuery searchQuery =
                new NativeSearchQueryBuilder()
                        .withQuery(QueryBuilders.matchQuery("desc", "我是"))
                        .withHighlightFields(new HighlightBuilder.Field("desc").preTags("<font color='red'>").postTags("</font>"))
                        .build();

        AggregatedPage<Stu> stus = esTemplate.queryForPage(searchQuery, Stu.class);
        for (Stu stu : stus) {
            System.out.println(stu);
        }
    }

    @Test
    public void testSearchItems() {
        SearchQuery searchQuery =
                new NativeSearchQueryBuilder()
                        .withQuery(QueryBuilders.matchQuery("itemName", "好吃"))
                        .build();

        AggregatedPage<Items> items = esTemplate.queryForPage(searchQuery, Items.class);
        for (Items itemsRes : items) {
            System.out.println(itemsRes);
        }
    }


}
