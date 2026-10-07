package com.sinka.platform.config;

import com.sinka.platform.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/regist")
                // Swagger 页面及其 JS、CSS 等静态资源
                .excludePathPatterns("/swagger-ui.html", "/swagger-ui/**")
                // springdoc 生成的接口说明 JSON 和页面配置
                .excludePathPatterns("/v3/api-docs", "/v3/api-docs/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/media/videos/**")
                .addResourceLocations("file:D:/platform-media/videos/");
    }
}
