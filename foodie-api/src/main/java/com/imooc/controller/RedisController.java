package com.imooc.controller;

import com.imooc.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@ApiIgnore
@RequestMapping("redis")
public class RedisController {

    private final static Logger logger = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(String key, String value){
        logger.info(" redis set----" + key + " " + value);

        redisOperator.set(key, value);

        return "Set OK";
    }

    @GetMapping("/get")
    public Object get(String key){
        logger.info(" redis get----");

        Object value = redisOperator.get(key);

        return value;
    }

    @GetMapping("/del")
    public Object del(String key){
        logger.info(" redis del----");

        boolean delete = redisOperator.del(key);
        return delete ? "Del OK" : "Del Fail";
    }

}
