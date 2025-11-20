package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.configuration.CheckPermission;
import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.account.AccountRequest;
import com.oliolishop.oliolishop.dto.account.AccountResponse;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.employee.EmployeeCreateRequest;
import com.oliolishop.oliolishop.dto.employee.EmployeeResponse;
import com.oliolishop.oliolishop.dto.role.RoleResponse;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.service.AccountService;
import com.oliolishop.oliolishop.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(ApiPath.Admin.ROOT)
public class AdminController {
    @Autowired
    EmployeeService employeeService;
    @Autowired
    AccountService accountService;

    @CheckPermission("EMPLOYEE_CREATE")
    @PostMapping(ApiPath.Admin.EMPLOYEE)
    public ApiResponse<EmployeeResponse> createEmployee(@RequestBody @Valid EmployeeCreateRequest request) {
        EmployeeResponse employee = employeeService.createEmployee(request);
        return ApiResponse.<EmployeeResponse>builder()
                .result(employee)
                .build();
    }

    @CheckPermission("EMPLOYEE_READ")
    @GetMapping(ApiPath.Admin.EMPLOYEE)
    public ApiResponse<PaginatedResponse<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Account.AccountStatus status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone
    ) {
        PaginatedResponse<EmployeeResponse> employeesPage = employeeService.getAllEmployees(page, size, status,name,phone);

        return ApiResponse.<PaginatedResponse<EmployeeResponse>>builder()
                .result(employeesPage)
                .build();
    }

    @CheckPermission("EMPLOYEE_READ")
    @GetMapping(ApiPath.Admin.EMPLOYEE + ApiPath.BY_ID)
    public ApiResponse<EmployeeResponse> getEmployeeById(@PathVariable String id) {
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.getEmployeeById(id))
                .build();
    }

    @CheckPermission("CUSTOMER_READ")
    @GetMapping(ApiPath.Admin.CUSTOMER)
    public ApiResponse<PaginatedResponse<AccountResponse>> getAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Account.AccountStatus status,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name) {
        return ApiResponse.<PaginatedResponse<AccountResponse>>builder()
                .result(accountService.getAllUsers(page, size, status, phone, name))
                .build();
    }

    @CheckPermission("CUSTOMER_CREATE")
    @PostMapping(ApiPath.Admin.CUSTOMER)
    public ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountRequest request) {

        return ApiResponse.<AccountResponse>builder().result(accountService.createAccount(request)).build();
    }

    @CheckPermission("CUSTOMER_READ")
    @GetMapping(ApiPath.Admin.CUSTOMER + ApiPath.BY_ID)
    public ApiResponse<AccountResponse> getAccountById(@PathVariable String id) {
        return ApiResponse.<AccountResponse>builder().result(accountService.getAccountById(id)).build();
    }

    @CheckPermission("CUSTOMER_DELETE")
    @PatchMapping(ApiPath.Admin.CUSTOMER + ApiPath.BY_ID+ApiPath.LOCK)
    public ApiResponse<String> deleteAccount(@PathVariable String id) {
        accountService.disableAccount(id);
        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.DELETE_SUCCESS, "Khóa tài khoản"))
                .build();
    }

    @CheckPermission("CUSTOMER_UPDATE")
    @PatchMapping(ApiPath.Admin.CUSTOMER + ApiPath.BY_ID+ApiPath.UNLOCK)
    public ApiResponse<String> unlockAccount(@PathVariable String id){
        accountService.enableAccount(id);
        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.SUCCESS,"Mở khóa tài khoản"))
                .build();
    }

    @CheckPermission("EMPLOYEE_UPDATE")
    @PatchMapping(ApiPath.Admin.EMPLOYEE + ApiPath.BY_ID + ApiPath.Admin.EMPLOYEE_ROLE)
    public ApiResponse<String> updateRoleEmployee(@PathVariable String id, @RequestParam String roleId) {
        employeeService.updateRoleForEmployee(id, roleId);
        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.SUCCESS, "Cập nhật quyền"))
                .build();
    }

    @CheckPermission("EMPLOYEE_DELETE")
    @PatchMapping(ApiPath.Admin.EMPLOYEE + ApiPath.BY_ID+ApiPath.LOCK)
    public ApiResponse<String> deleteEmployee(@PathVariable String id) {
        employeeService.disableEmployee(id);
        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.SUCCESS, "Khóa nhân viên"))
                .build();
    }

    @CheckPermission("EMPLOYEE_UPDATE")
    @PatchMapping(ApiPath.Admin.EMPLOYEE + ApiPath.BY_ID + ApiPath.UNLOCK)
    public ApiResponse<String> unLockEmployee(@PathVariable String id){
        employeeService.enableEmployee(id);
        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.SUCCESS,"Mở khóa nhân viên"))
                .build();
    }

    @GetMapping(ApiPath.Admin.EMPLOYEE+ApiPath.Admin.EMPLOYEE_ROLE)
    public ApiResponse<Set<RoleResponse>> getRoles()
    {
        return ApiResponse.<Set<RoleResponse>>builder()
                .result(employeeService.getRoles())
                .build();
    }

}
