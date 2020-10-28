package com.imooc.test;

import com.imooc.pojo.bo.ShopCartBO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class ListTest {

    @Test
    public void listTest(){
        ShopCartBO shopCartBO1 = new ShopCartBO();
        shopCartBO1.setItemId("aaa");

        ShopCartBO shopCartBO2 = new ShopCartBO();
        shopCartBO2.setItemId("aaa");

        List<ShopCartBO> list1 = new ArrayList<>();
        list1.add(shopCartBO1);

        List<ShopCartBO> list2 = new ArrayList<>();
        list2.add(shopCartBO2);

        list1.removeAll(list2);

        System.out.println(list1.size());

    }
}
