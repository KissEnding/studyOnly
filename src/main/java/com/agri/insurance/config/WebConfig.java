package com.agri.insurance.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer, ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        RequestMappingHandlerMapping requestMappingHandlerMapping = 
            event.getApplicationContext().getBean(RequestMappingHandlerMapping.class);
            
        log.info("Registered endpoints:");
        requestMappingHandlerMapping.getHandlerMethods().forEach((key, value) -> {
            log.info("{} -> {}", key, value);
        });
    }
} 