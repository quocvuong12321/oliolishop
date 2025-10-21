package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.location.DistrictDTO;
import com.oliolishop.oliolishop.dto.location.ProvinceDTO;
import com.oliolishop.oliolishop.dto.location.WardDTO;
import com.oliolishop.oliolishop.entity.District;
import com.oliolishop.oliolishop.entity.Province;
import com.oliolishop.oliolishop.entity.Ward;
import com.oliolishop.oliolishop.mapper.LocationMapper;
import com.oliolishop.oliolishop.repository.DistrictRepository;
import com.oliolishop.oliolishop.repository.ProvinceRepository;
import com.oliolishop.oliolishop.repository.WardRepository;
import org.springframework.cache.annotation.Cacheable; // 👈 Import này quan trọng
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@AllArgsConstructor
public class LocationService {

    ProvinceRepository provinceRepository;
    DistrictRepository districtRepository;
    WardRepository wardRepository;
    LocationMapper locationMapper;

    // Cache Name: provinces. Key: SimpleKey (do không có tham số)
    @Cacheable(value = "provinces")
    public List<ProvinceDTO> getAllProvinces() {
        List<Province> provinces = provinceRepository.findAll();
        return locationMapper.toProvinceDTOs(provinces);
    }

    // Cache Name: districtsByProvince. Key: ID của tỉnh (#provinceId)
    // Key trong Redis sẽ là: oliolishop:location:districtsByProvince::01
    @Cacheable(value = "districtsByProvince", key = "#provinceId")
    public List<DistrictDTO> getDistrictsByProvinceId(String provinceId) {
        List<District> districts = districtRepository.findByProvinceId(provinceId);
        return locationMapper.toDistrictDTOs(districts);
    }

    // Cache Name: wardsByDistrict. Key: ID của huyện (#districtId)
    // Key trong Redis sẽ là: oliolishop:location:wardsByDistrict::001
    @Cacheable(value = "wardsByDistrict", key = "#districtId")
    public List<WardDTO> getWardsByDistrictId(String districtId) {
        List<Ward> wards = wardRepository.findByDistrictId(districtId);
        return locationMapper.toWardDTOs(wards);
    }




}