package com.imooc.service.impl;

import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Stu getStuInfo(int id) {
        System.out.println("getStuInfo " + stuMapper);
        return stuMapper.selectByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveStu() {
        Stu stu = new Stu();
        stu.setAge(20);
        stu.setName("matrix");

        stuMapper.insert(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateStu(int id) {
        Stu stu = new Stu();
        stu.setId(id);
        stu.setAge(21);
        stu.setName("ming");

        stuMapper.updateByPrimaryKey(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteStu(int id) {
        stuMapper.deleteByPrimaryKey(id);
    }


    //TEST
    public void saveParent(){
        Stu stu = new Stu();
        stu.setName("Parent");
        stu.setAge(20);

        stuMapper.insert(stu);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveChild(){
        saveChild1();
        //int a = 1 / 0;
        saveChild2();
    }

    public void saveChild1(){
        Stu stu = new Stu();
        stu.setName("Child-1");
        stu.setAge(18);

        stuMapper.insert(stu);
    }

    public void saveChild2(){
        Stu stu = new Stu();
        stu.setName("Child-2");
        stu.setAge(17);

        stuMapper.insert(stu);
    }
}
