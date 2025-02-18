package com.agri.insurance.controller;

import com.agri.insurance.model.InsuredCrop;
import com.agri.insurance.service.InsuredCropService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
public class InsuredCropController {
    private final InsuredCropService cropService;

    @GetMapping
    public ResponseEntity<List<InsuredCrop>> getAllCrops(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(cropService.getCropsByCategory(category));
        }
        return ResponseEntity.ok(cropService.getAllCrops());
    }
} 