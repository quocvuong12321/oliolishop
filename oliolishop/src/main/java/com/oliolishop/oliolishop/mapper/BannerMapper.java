package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.banner.BannerRequest;
import com.oliolishop.oliolishop.dto.banner.BannerResponse;
import com.oliolishop.oliolishop.entity.Banner;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface BannerMapper {
    Banner toBanner(BannerRequest request);

    BannerResponse toResponse(Banner banner);
}
