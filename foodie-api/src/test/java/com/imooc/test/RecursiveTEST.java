package com.imooc.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RecursiveTEST {

    @Test
    public void test(){
        //10*9*8*7*6*5*4*3*2*1
        aa(10);
    }

    private int aa(int a){
        if(a > 1){
            a--;
            a *=aa(a);
            System.out.println("a=" + a);
        }

        return a;
    }
}
