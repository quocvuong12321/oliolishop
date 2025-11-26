package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.permission.PermissionRequest;
import com.oliolishop.oliolishop.dto.permission.PermissionResponse;
import com.oliolishop.oliolishop.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);

    PermissionResponse toResponse(Permission permission);

}
