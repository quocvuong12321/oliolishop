package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.location.DistrictDTO;
import com.oliolishop.oliolishop.dto.location.ProvinceDTO;
import com.oliolishop.oliolishop.dto.location.WardDTO;
import com.oliolishop.oliolishop.entity.District;
import com.oliolishop.oliolishop.entity.Province;
import com.oliolishop.oliolishop.entity.Ward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    // ------------------ Province Mappings ------------------
    
    ProvinceDTO toProvinceDTO(Province entity);

    List<ProvinceDTO> toProvinceDTOs(List<Province> entities);

    // ------------------ District Mappings ------------------

    @Mapping(source = "province.id", target = "provinceId")
    DistrictDTO toDistrictDTO(District entity);

    List<DistrictDTO> toDistrictDTOs(List<District> entities);

    // ------------------ Ward Mappings ------------------

    @Mapping(source = "district.id", target = "districtId")
    WardDTO toWardDTO(Ward entity);

    List<WardDTO> toWardDTOs(List<Ward> entities);
}