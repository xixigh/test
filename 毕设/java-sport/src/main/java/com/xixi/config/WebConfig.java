package com.xixi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域处理
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry
                //允许访问的路径
                .addMapping("/**")
                //配置请求来源
                .allowedOrigins("http://localhost:8083")
                //允许跨域访问的方法
                .allowedMethods("GET", "POST", "DELETE", "OPTION")
                //是否允许请求头
                //.allowCredentials(true)
                //最大响应时间
                .maxAge(6666);



    }
}
