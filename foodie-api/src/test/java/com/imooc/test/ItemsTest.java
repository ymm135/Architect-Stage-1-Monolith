package com.imooc.test;

import com.imooc.ApiApplication;
import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.service.ItemsService;
import com.imooc.utils.PagedGridResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
public class ItemsTest {

    @Autowired
    private ItemsService itemsService;

    @Test
    public void queryItems(){

        String itemId = "cake-1004";

        Items items = itemsService.queryItemById(itemId);
        System.out.println("" + items.getItemName() + " " + items.getId());

        List<ItemsImg> itemsImgs = itemsService.queryItemImgById(itemId);


        List<ItemsSpec> itemsSpecs = itemsService.queryItemSpecById(itemId);

        ItemsParam itemsParam = itemsService.queryItemParamById(itemId);

        System.out.println("End");
    }

    @Test
    public void getComments(){
        PagedGridResult itemsComments = itemsService.getItemsComments("cake-1001", 1,1, 5);
        System.out.println("");
    }

}
