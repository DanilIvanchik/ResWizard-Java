package com.reswizard.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.avatar.path}")
    private String avatarUploadPath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/static/images/**")
                .addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file://"+avatarUploadPath+"/");

    }
}
