package com.imooc.config;

import com.imooc.service.OrderService;
import com.imooc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
public class OrderTask {

    @Autowired
    private OrderService orderService;

//    @Scheduled(cron = "0/3 * * * * ?")
    public void cleanOrder(){

        orderService.closeOrders();

        System.out.println("定时任务cleanOrder " + DateUtil.dateToString(new Date(), DateUtil.DATETIME_PATTERN));
    }
}
