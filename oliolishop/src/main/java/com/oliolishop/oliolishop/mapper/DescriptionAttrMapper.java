package com.oliolishop.oliolishop.mapper;

import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrRequest;
import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrResponse;
import com.oliolishop.oliolishop.entity.DescriptionAttr;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface DescriptionAttrMapper {

    DescriptionAttrResponse toResponse(DescriptionAttr descriptionAttr);

    DescriptionAttr toDescriptionAttr(DescriptionAttrRequest request);

}
