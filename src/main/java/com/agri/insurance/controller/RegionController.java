package com.agri.insurance.controller;

import com.agri.insurance.model.Region;
import com.agri.insurance.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
@CrossOrigin
public class RegionController {
    private static final Logger log = LoggerFactory.getLogger(RegionController.class);
    private final RegionService regionService;

    @GetMapping("/provinces")
    public ResponseEntity<List<Region>> getProvinces() {
        try {
            List<Region> provinces = regionService.getProvinces();
            return ResponseEntity.ok(provinces);
        } catch (Exception e) {
            log.error("Error getting provinces", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cities")
    public ResponseEntity<List<Region>> getCities(@RequestParam(required = true) String provinceCode) {
        try {
            if (provinceCode == null || provinceCode.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<Region> cities = regionService.getCities(provinceCode);
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            log.error("Error getting cities for province: {}", provinceCode, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/districts")
    public ResponseEntity<List<Region>> getDistricts(@RequestParam(required = true) String cityCode) {
        try {
            if (cityCode == null || cityCode.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<Region> districts = regionService.getDistricts(cityCode);
            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            log.error("Error getting districts for city: {}", cityCode, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{code}")
    public ResponseEntity<Region> getRegion(@PathVariable String code) {
        Region region = regionService.getRegion(code);
        return ResponseEntity.ok(region);
    }
} 