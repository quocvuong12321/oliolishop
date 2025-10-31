package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.employee.EmployeeCreateRequest;
import com.oliolishop.oliolishop.dto.employee.EmployeeResponse;
import com.oliolishop.oliolishop.repository.EmployeeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    EmployeeRepository employeeRepository;

//    @Transactional
//    public EmployeeResponse createEmployee(EmployeeCreateRequest request){
//
//
//
//    }


}
