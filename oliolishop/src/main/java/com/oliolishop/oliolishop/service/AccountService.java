package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.account.AccountRequest;
import com.oliolishop.oliolishop.dto.account.AccountResponse;
import com.oliolishop.oliolishop.dto.account.AccountUpdateRequest;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Customer;
import com.oliolishop.oliolishop.entity.Role;
import com.oliolishop.oliolishop.enums.VoucherStatus;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.AccountMapper;
import com.oliolishop.oliolishop.mapper.CustomerMapper;
import com.oliolishop.oliolishop.repository.AccountRepository;
import com.oliolishop.oliolishop.repository.CustomerRepository;
import com.oliolishop.oliolishop.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AccountService {


    AccountRepository accountRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    public PaginatedResponse<AccountResponse> getAllUsers(int page, int size, Account.AccountStatus status) {

        Pageable pageable = PageRequest.of(page,size);

        Page<Account> accounts;

        if(status!= null){
            accounts = accountRepository.findByStatus(status,pageable);
        }
        else {
            accounts = accountRepository.findAll(pageable);
        }

        Page<AccountResponse> accountResponses = accounts
                .map(
                        account -> {
                            AccountResponse response = accountMapper.toAccountResponse(account);
                            if (account.getCustomer() != null) {
                                response.setCustomerResponse(customerMapper.toResponse(account.getCustomer()));
                            }
                            return response;
                        }
                );

        return PaginatedResponse.fromSpringPage(accountResponses);
    }

    public AccountResponse getAccountById(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));
        AccountResponse response = accountMapper.toAccountResponse(account);

        // Thêm thông tin Customer nếu có
        if (account.getCustomer() != null) {
            response.setCustomerResponse(customerMapper.toResponse(account.getCustomer()));
        }

        return response;
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        // 1. Kiểm tra tồn tại
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException((ErrorCode.ACCOUNT_EXISTED));
        }

        // 2. Tạo Entity Account
        Account newAccount = accountMapper.toAccount(request);
        newAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        newAccount.setStatus(Account.AccountStatus.Active);

        newAccount.setId(UUID.randomUUID().toString());
        Customer customer = Customer.builder()
                .id(UUID.randomUUID().toString())
                .account(newAccount)
                .build();

        Account savedAccount = accountRepository.save(newAccount);
        customerRepository.save(customer);

        // 5. Mapping và trả về
        AccountResponse response = accountMapper.toAccountResponse(savedAccount);
        response.setCustomerResponse(customerMapper.toResponse(savedAccount.getCustomer()));
        return response;
    }

    /**
     * Vô hiệu hóa (Disable) một Account (Soft Delete).
     */
    @Transactional
    public void disableAccount(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        // Chỉ vô hiệu hóa, không xóa khỏi DB
        account.setStatus(Account.AccountStatus.Inactive);
        accountRepository.save(account);
    }

}
