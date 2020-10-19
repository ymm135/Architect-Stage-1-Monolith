package com.imooc.common;

import java.io.File;

public class Common {

    //支付后支付中心回调的Url   支付成功->支付中心->天天吃货平台->回调的Url
    //121.196.39.142 阿里云
    public static String PAY_RETURN_URL = "http://121.196.39.142:8088/foodie-api/orders/notifyMerchantOrderPaid";

    public static String PAYMENT_URL = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";


    /**
     * 支付中心的账号
     */
    public static String USER_NAME = "7784877-1210919685";
    public static String USER_PWD = "t53i-90ty-i640-8964";


    /**
     * 用户头像保存地址
     */
    public static String USER_FACE_LOCATION = "/Users/zero/work" + File.separator + "mooc/image";



    public static int DEFAULT_PAGESIZE = 20;


}
