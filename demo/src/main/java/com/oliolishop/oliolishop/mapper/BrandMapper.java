package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.brand.BrandRequest;
import com.oliolishop.oliolishop.dto.brand.BrandResponse;
import com.oliolishop.oliolishop.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandResponse toResponse(Brand brand);
    Brand toBrand(BrandRequest request);
}
