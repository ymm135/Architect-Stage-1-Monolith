package com.imooc.test;

import com.imooc.enums.YesOrNo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EnumTest {

    @Test
    public void test(){
        System.out.println(YesOrNo.values());
    }

}
