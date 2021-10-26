package com.imooc.config;

import com.imooc.controller.interceptor.UserTokenInterceptor;
import com.imooc.resources.FileUploadResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Autowired
    private FileUploadResources fileUploadResources;

    @Bean
    public RestTemplate getRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * 实现静态资源的注册
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //需要设置是路径，最后要有"/"
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + fileUploadResources.getSavePath() + File.separator); //映射本地静态资源

        registry.addResourceHandler("swagger-ui.html", "doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");//映射Swagger2,它也是通过静态html资源访问的
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor(){
        return new UserTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/center/*")
                .addPathPatterns("/mycomments/*")
                .addPathPatterns("/userInfo/*")
                .addPathPatterns("/myorders/*")
                .addPathPatterns("/order/*");

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
