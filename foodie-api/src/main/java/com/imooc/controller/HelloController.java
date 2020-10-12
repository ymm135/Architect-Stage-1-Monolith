package com.imooc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@ApiIgnore
public class HelloController {

    private final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    public String hello(){
        new Throwable("matrix").printStackTrace();

        logger.info("info----");
        logger.error("error----");
        logger.warn("warn----");
        logger.debug("debug----");
        return "Hello Spring5";
    }

    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request){

        HttpSession session = request.getSession();
        session.setAttribute("userInfo","new user");
        session.setMaxInactiveInterval(3600);
        Object userInfo = session.getAttribute("userInfo");
        //session.removeAttribute("userInfo");

        return "ok";
    }

}
