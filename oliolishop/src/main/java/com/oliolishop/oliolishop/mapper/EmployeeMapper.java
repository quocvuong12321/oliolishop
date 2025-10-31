package com.oliolishop.oliolishop.mapper;

import com.oliolishop.oliolishop.dto.employee.EmployeeCreateRequest;
import com.oliolishop.oliolishop.dto.employee.EmployeeResponse;
import com.oliolishop.oliolishop.entity.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface EmployeeMapper {

    Employee toEmployee(EmployeeCreateRequest request);

    EmployeeResponse toResponse(Employee employee);


}
