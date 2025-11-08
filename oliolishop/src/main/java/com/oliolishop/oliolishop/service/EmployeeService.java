package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.employee.ChangePasswordRequest;
import com.oliolishop.oliolishop.dto.employee.EmployeeCreateRequest;
import com.oliolishop.oliolishop.dto.employee.EmployeeResponse;
import com.oliolishop.oliolishop.dto.employee.EmployeeUpdateRequest;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Employee;
import com.oliolishop.oliolishop.entity.Role;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.EmployeeMapper;
import com.oliolishop.oliolishop.repository.EmployeeRepository;
import com.oliolishop.oliolishop.repository.RoleRepository;
import com.oliolishop.oliolishop.util.AppUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeService {
    String defaultPassword = "abcd123456";
    EmployeeMapper employeeMapper;
    EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {

        Employee employee = employeeMapper.toEmployee(request);

        employee.setUsername(request.getPhoneNumber());



        employee.setPassword(passwordEncoder.encode(defaultPassword));

        employee.setStatus(Account.AccountStatus.Active);

        Role r = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));

        employee.setRole(r);

        return employeeMapper.toResponse(employeeRepository.save(employee));

    }

    public EmployeeResponse getEmployeeById(String id){

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXIST));

        return employeeMapper.toResponse(employee);

    }

    // --- GET LIST & PAGINATION ---
    public PaginatedResponse<EmployeeResponse> getAllEmployees(int page, int size, Account.AccountStatus status) {
        // Có thể thêm lọc theo trạng thái enabled = true nếu cần
        Pageable pageable = PageRequest.of(page, size);

        Page<EmployeeResponse> employeePage;
        if (status != null) {
            employeePage = employeeRepository.findByStatus(status, pageable).map(employeeMapper::toResponse);
        } else {
            employeePage = employeeRepository.findAll(pageable).map(employeeMapper::toResponse);
        }

        return PaginatedResponse.fromSpringPage(employeePage);
    }


    @Transactional
    public void disableEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXIST));

        // Vô hiệu hóa tài khoản
        employee.setStatus(Account.AccountStatus.Inactive);
        employeeRepository.save(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(EmployeeUpdateRequest request){

        String employeeId = AppUtils.getEmployeeIdByJwt();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXIST));

        employee.setName(request.getName());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setEmail(request.getEmail());
        employee.setUsername(request.getPhoneNumber());

        return employeeMapper.toResponse(employeeRepository.save(employee));

    }

    @Transactional
    public void updatePassword(ChangePasswordRequest request){

        String employeeId = AppUtils.getEmployeeIdByJwt();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXIST));
        if (request.getNewPassword().equals(defaultPassword))
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        if(!passwordEncoder.matches(request.getOldPassword(),employee.getPassword()))
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));

        employeeRepository.save(employee);

    }

    public EmployeeResponse getProfile(){
        String employeeId = AppUtils.getEmployeeIdByJwt();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXIST));

        return employeeMapper.toResponse(employee);

    }

    public void updateRoleForEmployee(String employeeId, String roleId){
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXIST));

        Role r = roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
        employee.setRole(r);
        employeeRepository.save(employee);
    }


}
