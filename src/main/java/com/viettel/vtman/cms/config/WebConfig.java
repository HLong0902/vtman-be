package com.viettel.vtman.cms.config;

import com.viettel.vtman.cms.utils.ApplicationConfigurationProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationConfigurationProp configurationProp;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (configurationProp.getEnvironmentUploadFile().equals("LINUX")) {
            registry.addResourceHandler(configurationProp.getPathDownload() + "/**").addResourceLocations("file:" + configurationProp.getPathUploadFileExtention());
        } else {
            registry.addResourceHandler(configurationProp.getPathDownload() + "/**").addResourceLocations("file:/" + System.getProperty("user.dir") + configurationProp.getPathUpload());
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
                .allowCredentials(true);
    }

}
