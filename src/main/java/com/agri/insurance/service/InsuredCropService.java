package com.agri.insurance.service;

import com.agri.insurance.model.InsuredCrop;
import com.agri.insurance.repository.InsuredCropRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsuredCropService {
    private final InsuredCropRepository cropRepository;

    public List<InsuredCrop> getAllCrops() {
        return cropRepository.findAll();
    }

    public List<InsuredCrop> getCropsByCategory(String category) {
        return cropRepository.findByCropCategory(category);
    }
} 