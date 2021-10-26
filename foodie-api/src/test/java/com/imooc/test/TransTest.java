package com.imooc.test;

import com.imooc.ApiApplication;
import com.imooc.service.StuService;
import com.imooc.service.TestTransService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
public class TransTest {

    @Autowired
    private StuService stuService;

    @Autowired
    private TestTransService testTransService;

    @Test
    public void test1(){
        testTransService.testPropagationTrans();
    }
}
