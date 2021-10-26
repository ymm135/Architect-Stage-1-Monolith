package com.imooc.config;

import com.imooc.plugin.MyBatisPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class MybatisConfig {

    //注册插件
    @Bean
    public MyBatisPlugin myPlugin() {
        MyBatisPlugin myPlugin = new MyBatisPlugin();
        //设置参数，比如阈值等，可以在配置文件中配置，这里直接写死便于测试
        Properties properties = new Properties();
        //这里设置慢查询阈值为1毫秒，便于测试
        properties.setProperty("time", "1");
        myPlugin.setProperties(properties);
        return myPlugin;
    }
}
