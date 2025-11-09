package com.oliolishop.oliolishop.service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Employee;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.repository.EmployeeRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class EmployeeAuthenticationService extends BaseAuthenticationService<Employee>{
    EmployeeRepository employeeRepository;

    public EmployeeAuthenticationService(
            EmployeeRepository employeeRepository,
        RefreshTokenService refreshTokenService
    ) {
        super(refreshTokenService);
        this.employeeRepository=employeeRepository;
    }

    @Override
    protected void addIdClaims(JWTClaimsSet.Builder builder, Employee employee) {
        builder.claim("employeeId",employee.getId());
    }

    @Override
    protected Employee findUserByUsername(String username) {
        return employeeRepository.findByUsername(username).orElseThrow(()->new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));
    }

    @Override
    protected String getUsername(Employee user) {
        return user.getUsername();
    }

    @Override
    protected String getPassword(Employee user) {
        return user.getPassword();
    }

    @Override
    protected String buildScope(Employee user) {
        return "ROLE_"+user.getRole().getName();
    }

    @Override
    protected Account.AccountStatus getStatus(Employee user) {
        return user.getStatus();
    }

}
