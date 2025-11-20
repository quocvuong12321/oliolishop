package com.oliolishop.oliolishop.configuration;

import com.oliolishop.oliolishop.entity.Permission;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;

    @Around("@annotation(checkPermission)")
    public Object check(ProceedingJoinPoint joinPoint, CheckPermission checkPermission) throws Throwable {
        // Lấy roleId từ Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String scope = "";
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            scope = jwt.getClaim("scope");
            if (scope == null) throw new AppException(ErrorCode.UNAUTHORIZED);
            scope = scope.replace("ROLE_", "");
        }

        Set<String> permissions = permissionService.getPermissionsByRole(scope);


        // Kiểm tra
        if (!permissions.contains(checkPermission.value())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return joinPoint.proceed();
    }
}
