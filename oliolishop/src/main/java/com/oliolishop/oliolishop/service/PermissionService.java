package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.entity.Permission;
import com.oliolishop.oliolishop.entity.Role;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.repository.PermissionRepository;
import com.oliolishop.oliolishop.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    RedisService redisService;
    final String ROLE_KEY_PREFIX = "role_permissions:";
    private final RoleRepository roleRepository;

    public Set<String> getPermissionsByRole(String roleName) {
        String key = ROLE_KEY_PREFIX + roleName;

        // 1. Kiểm tra Redis trước
        List<String> permissions = redisService.get(key, ArrayList.class);
        if (permissions != null) {
            return new HashSet<>(permissions);
        }

        // 2. Nếu chưa có cache → lấy từ DB
        Role role = roleRepository.findByName(roleName);

        Set<String> permissionNames = Optional.ofNullable(role.getPermissions())
                .orElse(Collections.emptySet())
                .stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());

        // 3. Lưu cache vào Redis
       redisService.set(key, new ArrayList<>(permissionNames), 30*60);

        return permissionNames;
    }

}
