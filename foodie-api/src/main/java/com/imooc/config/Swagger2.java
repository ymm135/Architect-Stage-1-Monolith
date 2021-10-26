package com.imooc.config;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {
    //配置swagger核心配置
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2) //指定api类型为swagger2
                .apiInfo(apiInfo()) //api详细信息
                .select().apis(RequestHandlerSelectors.basePackage("com.imooc.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    public ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("天天吃货平台接口API")
                .contact(new Contact("xiaoming","http://imooc.com","1210919685@qq.com"))
                .description("这是是给前端人员看的！")
                .version("1.0.1")
                .termsOfServiceUrl("http://imooc.com")
                .build();
    }

}
