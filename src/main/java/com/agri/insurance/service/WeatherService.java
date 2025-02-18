package com.agri.insurance.service;

import com.agri.insurance.dto.WeatherResponse;
import com.agri.insurance.model.Farmer;
import com.agri.insurance.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    
    @Value("${weather.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    private final FarmerRepository farmerRepository;
    
    // 城市名称映射
    private static final Map<String, String> CITY_MAPPING = new HashMap<>();
    static {
        CITY_MAPPING.put("杭州", "hangzhou");
        CITY_MAPPING.put("宁波", "ningbo");
        CITY_MAPPING.put("温州", "wenzhou");
        CITY_MAPPING.put("嘉兴", "jiaxing");
        CITY_MAPPING.put("湖州", "huzhou");
        CITY_MAPPING.put("绍兴", "shaoxing");
        CITY_MAPPING.put("金华", "jinhua");
        CITY_MAPPING.put("衢州", "quzhou");
        CITY_MAPPING.put("舟山", "zhoushan");
        CITY_MAPPING.put("台州", "taizhou");
        CITY_MAPPING.put("丽水", "lishui");
        // 可以继续添加更多城市映射
    }
    
    private String extractCity(String location) {
        if (location == null || location.trim().isEmpty()) {
            log.warn("Location is empty, using default city");
            return "杭州";  // 默认城市
        }
        
        log.info("Extracting city from location: {}", location);
        
        // 处理完整的地址格式：浙江省宁波市江北区
        String[] parts = location.split("省|市|区");
        log.info("Split parts: {}", String.join(", ", parts));
        
        if (parts.length >= 2) {
            String city = parts[1].trim();
            if (!city.isEmpty()) {
                log.info("Successfully extracted city: {}", city);
                return city;
            }
        }
        
        // 如果上面的方法失败，尝试直接匹配已知城市
        for (String knownCity : CITY_MAPPING.keySet()) {
            if (location.contains(knownCity)) {
                log.info("Found matching city in location: {}", knownCity);
                return knownCity;
            }
        }
        
        log.warn("Failed to extract city from location: {}, using default city", location);
        return "杭州";  // 如果无法解析，返回默认城市
    }
    
    private String convertToPinyin(String chineseCity) {
        if (chineseCity == null || chineseCity.trim().isEmpty()) {
            return "hangzhou";
        }
        return CITY_MAPPING.getOrDefault(chineseCity.trim(), "hangzhou");
    }
    
    public WeatherResponse getWeatherForFarmer(String username) {
        log.info("Getting weather for farmer with username: {}", username);
        
        // 通过用户名查找农户
        Farmer farmer = farmerRepository.findByUsername(username)
            .orElse(null);
            
        if (farmer == null) {
            log.warn("Farmer not found for username: {}, using default city", username);
            return getWeather("杭州");
        }
        
        String location = farmer.getLocation();
        if (location == null || location.trim().isEmpty()) {
            log.warn("Location is empty for farmer: {}, using default city", username);
            return getWeather("杭州");
        }
        
        log.info("Found farmer location: {}", location);
        
        // 从地址中提取城市名
        String city = extractCity(location);
        log.info("Extracted city '{}' from location '{}' for farmer: {}", 
            city, location, username);
        
        // 转换为拼音并获取天气
        String cityPinyin = convertToPinyin(city);
        log.info("Converted city name to pinyin: {} -> {}", city, cityPinyin);
        
        WeatherResponse response = getWeather(cityPinyin);
        log.info("Weather response for {}: {}", cityPinyin, response);
        
        return response;
    }
    
    public WeatherResponse getWeather(String location) {
        // 如果已经是拼音，就不需要再转换
        String cityPinyin = CITY_MAPPING.containsValue(location) ? location : convertToPinyin(location);
        
        String url = UriComponentsBuilder
            .fromHttpUrl("https://api.seniverse.com/v3/weather/daily.json")
            .queryParam("key", apiKey)
            .queryParam("location", cityPinyin)
            .queryParam("language", "zh-Hans")
            .queryParam("unit", "c")
            .queryParam("start", "0")
            .queryParam("days", "1")
            .queryParam("fields", "all")
            .build()
            .encode()
            .toUriString();
        
        try {
            log.info("Fetching weather data for location: {} ({})", location, cityPinyin);
            log.info("Request URL: {}", url);
            
            ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(url, WeatherResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Weather response: {}", response.getBody());
                return response.getBody();
            } else {
                log.error("Failed to fetch weather data. Status: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to fetch weather data for {} ({}): {}", location, cityPinyin, e.getMessage(), e);
            return null;
        }
    }
} 