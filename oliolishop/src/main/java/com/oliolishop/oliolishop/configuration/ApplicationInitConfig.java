package com.oliolishop.oliolishop.configuration;

import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Employee;
import com.oliolishop.oliolishop.entity.Role;
import com.oliolishop.oliolishop.enums.RoleEnum;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.repository.EmployeeRepository;
import com.oliolishop.oliolishop.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(EmployeeRepository employeeRepository,
                                        RoleRepository roleRepository) {
        return args -> {
            if (employeeRepository.findByUsername("admin").isEmpty()) {

                Role r = roleRepository.findById(RoleEnum.ADMIN.name())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));

                String username = "admin";

                Employee employeeAdmin = Employee.builder()
                        .role(r)
                        .username(username)
                        .password(passwordEncoder.encode("admin"))
                        .status(Account.AccountStatus.Active)
                        .build();

                employeeRepository.save(employeeAdmin);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}
