package com.imooc.test;

import com.imooc.enums.YesOrNo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(JUnit4.class)
public class EnumTest {

    @Test
    public void test() throws IOException {
        System.out.println(YesOrNo.values());

        String path = "ddd-dd.ddd.png";
        String[] split = path.split("\\.");

        System.out.println(split[split.length - 1]);
    }

}
