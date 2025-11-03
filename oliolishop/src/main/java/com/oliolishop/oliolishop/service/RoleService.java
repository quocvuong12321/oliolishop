package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.role.RoleResponse;
import com.oliolishop.oliolishop.entity.Role;
import com.oliolishop.oliolishop.mapper.RoleMapper;
import com.oliolishop.oliolishop.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public PaginatedResponse<RoleResponse> getRoles(int page,int size){

        Pageable pageable = PageRequest.of(page,size);
        Page<RoleResponse> roles = roleRepository.findAll(pageable).map(roleMapper::toResponse);

        return PaginatedResponse.fromSpringPage(roles);
    }


    public RoleResponse getRoleByName(String name){

        Role role = roleRepository.findByName(name);
        return roleMapper.toResponse(role);
    }

}
