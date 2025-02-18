package com.agri.insurance.controller;

import com.agri.insurance.dto.WeatherResponse;
import com.agri.insurance.service.WeatherService;
import com.agri.insurance.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Slf4j
@RestController
@RequestMapping("/api/weather")
@CrossOrigin
@RequiredArgsConstructor
public class WeatherController {
    
    private final WeatherService weatherService;
    
    @GetMapping("/farmer")
    public WeatherResponse getWeatherForFarmer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Current authentication: principal={}, name={}, authorities={}", 
            authentication.getPrincipal(),
            username,
            authentication.getAuthorities()
        );
        log.info("Getting weather for farmer username: {}", username);
        return weatherService.getWeatherForFarmer(username);
    }
} 