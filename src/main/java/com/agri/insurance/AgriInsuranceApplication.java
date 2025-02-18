package com.agri.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("com.agri.insurance.model")
@EnableJpaRepositories("com.agri.insurance.repository")
@ComponentScan(basePackages = "com.agri.insurance")
public class AgriInsuranceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriInsuranceApplication.class, args);
    }
} 