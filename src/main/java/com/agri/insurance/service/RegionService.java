package com.agri.insurance.service;

import com.agri.insurance.model.Region;
import com.agri.insurance.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionService {
    private final RegionRepository regionRepository;

    @Transactional(readOnly = true)
    public List<Region> getProvinces() {
        return regionRepository.findByLevelOrderByCode("province");
    }

    @Transactional(readOnly = true)
    public List<Region> getCities(String provinceCode) {
        try {
            if (provinceCode == null || provinceCode.isEmpty()) {
                throw new IllegalArgumentException("Province code cannot be empty");
            }
            log.debug("Getting cities for province: {}", provinceCode);
            List<Region> cities = regionRepository.findByParentCodeOrderByCode(provinceCode);
            log.debug("Found {} cities", cities.size());
            return cities;
        } catch (Exception e) {
            log.error("Error getting cities for province: {}", provinceCode, e);
            throw new RuntimeException("Failed to get cities", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Region> getDistricts(String cityCode) {
        return regionRepository.findByParentCodeOrderByCode(cityCode);
    }

    @Transactional(readOnly = true)
    public Region getRegion(String code) {
        return regionRepository.findByCode(code)
            .orElseThrow(() -> new EntityNotFoundException("Region not found with code: " + code));
    }

    @Transactional(readOnly = true)
    public List<Region> getChildren(String parentCode) {
        return regionRepository.findByParentCodeOrderByCode(parentCode);
    }

    @Transactional(readOnly = true)
    public boolean validateArea(String provinceCode, String cityCode, String districtCode) {
        try {
            if (provinceCode == null) {
                log.warn("Province code is null");
                return false;
            }

            // 如果只选择了省份
            if (cityCode == null) {
                Region province = regionRepository.findByCode(provinceCode)
                    .orElse(null);
                if (province == null) {
                    log.warn("Province not found: {}", provinceCode);
                    return false;
                }
                return "province".equals(province.getLevel());
            }

            // 如果选择了城市，验证城市是否属于该省份
            Region city = regionRepository.findByCode(cityCode)
                .orElse(null);
            if (city == null) {
                log.warn("City not found: {}", cityCode);
                return false;
            }
            if (!"city".equals(city.getLevel()) || !provinceCode.equals(city.getParentCode())) {
                log.warn("Invalid city. Level: {}, ParentCode: {}", city.getLevel(), city.getParentCode());
                return false;
            }

            // 如果选择了区县，验证区县是否属于该城市
            if (districtCode != null) {
                Region district = regionRepository.findByCode(districtCode)
                    .orElse(null);
                if (district == null) {
                    log.warn("District not found: {}", districtCode);
                    return false;
                }
                if (!"district".equals(district.getLevel()) || !cityCode.equals(district.getParentCode())) {
                    log.warn("Invalid district. Level: {}, ParentCode: {}", district.getLevel(), district.getParentCode());
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Error validating area codes", e);
            return false;
        }
    }

    public String getRegionName(String code) {
        return regionRepository.findByCode(code)
            .map(Region::getName)
            .orElse("");
    }
} 