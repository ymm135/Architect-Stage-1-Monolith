package com.imooc.test;

import com.imooc.ApiApplication;
import com.imooc.controller.PassportController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
public class HandleMethodTest {
    @Autowired
    private PassportController passportController;

    @Test
    public void test() throws NoSuchMethodException {
        Method usernameIsExist = passportController.getClass().getMethod("usernameIsExist", String.class);
        HandlerMethod handlerMethod = new HandlerMethod(passportController, usernameIsExist);

        MethodParameter methodParameter = handlerMethod.getReturnType().nestedIfOptional();
    }
}
