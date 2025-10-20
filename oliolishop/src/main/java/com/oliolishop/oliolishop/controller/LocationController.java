package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.location.DistrictDTO;
import com.oliolishop.oliolishop.dto.location.ProvinceDTO;
import com.oliolishop.oliolishop.dto.location.WardDTO;
import com.oliolishop.oliolishop.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPath.Location.ROOT)
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping("/provinces")
    public ApiResponse<List<ProvinceDTO>> getAllProvinces() {
        List<ProvinceDTO> provinces = locationService.getAllProvinces();
        return ApiResponse.<List<ProvinceDTO>>builder()
                .result(provinces)
                .build();
    }

    /**
     * Lấy danh sách Huyện/Quận theo ID Tỉnh
     * API: GET /api/locations/provinces/{provinceId}/districts
     */
    @GetMapping(ApiPath.Location.DISTRICTS_BY_PROVINCE)
    public ApiResponse<List<DistrictDTO>> getDistrictsByProvinceId(
            @PathVariable String provinceId) {

        List<DistrictDTO> districts = locationService.getDistrictsByProvinceId(provinceId);
        return ApiResponse.<List<DistrictDTO>>builder()
                .result(districts)
                .build();
    }

    /**
     * Lấy danh sách Xã/Phường theo ID Huyện
     * API: GET /api/locations/districts/{districtId}/wards
     */
    @GetMapping(ApiPath.Location.WARDS_BY_DISTRICT)
    public ApiResponse<List<WardDTO>> getWardsByDistrictId(
            @PathVariable String districtId) {

        List<WardDTO> wards = locationService.getWardsByDistrictId(districtId);
        return ApiResponse.<List<WardDTO>>builder()
                .result(wards)
                .build();
    }
}
