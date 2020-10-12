package com.imooc.service;

import com.imooc.service.impl.StuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestTransService {

    @Autowired
    private StuServiceImpl stuService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void testPropagationTrans(){
        System.out.println("testPropagationTrans");
        stuService.saveParent();
        stuService.saveChild();

    }
}
