package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.role.RoleRequest;
import com.oliolishop.oliolishop.dto.role.RoleResponse;
import com.oliolishop.oliolishop.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface RoleMapper {

    Role toRole(RoleRequest request);

    RoleResponse toResponse(Role role);

}
